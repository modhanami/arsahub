package com.arsahub.backend.services.ruleengine

import com.arsahub.backend.dtos.request.TriggerSendRequest
import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.RuleProgress
import com.arsahub.backend.models.RuleRepeatability
import com.arsahub.backend.models.Trigger
import com.arsahub.backend.models.TriggerLog
import com.arsahub.backend.repositories.RuleProgressRepository
import com.arsahub.backend.repositories.TriggerLogRepository
import com.arsahub.backend.services.RuleService
import com.arsahub.backend.services.TriggerService
import com.arsahub.backend.services.actionhandlers.ActionHandlerRegistry
import com.arsahub.backend.services.actionhandlers.ActionResult
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RuleEngine(
    private val actionHandlerRegistry: ActionHandlerRegistry,
    private val triggerLogRepository: TriggerLogRepository,
    private val ruleProgressRepository: RuleProgressRepository,
    private val triggerService: TriggerService,
    private val ruleService: RuleService,
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun trigger(
        app: App,
        appUser: AppUser,
        request: TriggerSendRequest,
        rawRequestJson: Map<String, Any>,
        afterAction: (ActionResult) -> Unit,
    ) {
        val trigger = triggerService.getTriggerOrThrow(request.key!!, app)

        logTrigger(trigger, app, appUser, rawRequestJson)

        triggerService.validateParamsAgainstTriggerFields(request.params, trigger.fields)

        val matchingRules = ruleService.getMatchingRules(app, trigger)
        val actionResults = processMatchingRules(matchingRules, app, appUser, request.params, afterAction)

        handleForwardChain(app, appUser, actionResults, afterAction)

        /*
         TODO: Currently, the trigger log is committed as a whole with other operations.
               Could possibly let Kafka handle this in the future and separate ingestion from processing.
         */
    }

    private fun processMatchingRules(
        matchingRules: List<Rule>,
        app: App,
        appUser: AppUser,
        params: Map<String, Any>?,
        afterAction: (ActionResult) -> Unit?,
    ): List<ActionResult> {
        val actionResults = mutableListOf<ActionResult>()

        for (rule in matchingRules) {
            logger.debug { "Checking rule ${rule.title} (${rule.id})" }

            if (
                // check repeatability
                !validateRepeatability(rule, appUser) ||
                // check the params against the rule conditions, if any
                !validateConditions(rule, appUser, params)
            ) {
                //  not repeatable or conditions don't match
                continue
            }

            // handle action
            val actionResult = activateRule(rule, app, appUser)
            actionResults.add(actionResult)

            afterAction(actionResult)
        }

        return actionResults
    }

    private fun handleForwardChain(
        app: App,
        appUser: AppUser,
        actionResults: List<ActionResult>,
        afterAction: (ActionResult) -> Unit?,
    ) {
        logger.info { "Handling forward chain" }
        val needToTriggerPointsReachedTrigger = actionResults.any { it is ActionResult.PointsUpdate }
        logger.debug { "Need to trigger points_reached trigger: $needToTriggerPointsReachedTrigger" }
        if (!needToTriggerPointsReachedTrigger) {
            return
        }

        val pointsReachedTrigger = triggerService.getBuiltInTriggerOrThrow("points_reached")
        val matchingRules = ruleService.getMatchingRules(app, pointsReachedTrigger)

        logger.debug { "Found ${matchingRules.size} matching rules for points_reached trigger" }
        processMatchingRules(matchingRules, app, appUser, emptyMap(), afterAction)
    }

    private fun logTrigger(
        trigger: Trigger,
        app: App,
        appUser: AppUser,
        rawRequestJson: Map<String, Any>,
    ): TriggerLog {
        val triggerLog =
            TriggerLog(
                trigger = trigger,
                requestBody = rawRequestJson.toMutableMap(),
                app = app,
                appUser = appUser,
            )
        triggerLogRepository.save(triggerLog)
        logger.debug {
            "Received trigger ${trigger.title} (${trigger.id}) for user ${appUser.userId} (${appUser.id}) " +
                "from app ${app.title} (${app.id})"
        }

        return triggerLog
    }

    private fun activateRule(
        rule: Rule,
        app: App,
        appUser: AppUser,
    ): ActionResult {
        val actionResult = actionHandlerRegistry.handleAction(rule, appUser)

        // update rule progress
        progressRule(rule, app, appUser)

        return actionResult
    }

    private fun progressRule(
        rule: Rule,
        app: App,
        appUser: AppUser,
    ): RuleProgress {
        val ruleProgress =
            ruleProgressRepository.findByRuleAndAppUser(rule, appUser) ?: RuleProgress(
                rule = rule,
                app = app,
                appUser = appUser,
            )
        ruleProgress.activationCount = (ruleProgress.activationCount ?: 0) + 1
        return ruleProgressRepository.save(ruleProgress)
    }

    private fun validateRepeatability(
        rule: Rule,
        appUser: AppUser,
    ): Boolean {
        if (rule.repeatability == RuleRepeatability.ONCE_PER_USER) {
            val ruleProgress = ruleProgressRepository.findByRuleAndAppUser(rule, appUser)
            if (ruleProgress != null && ruleProgress.activationCount!! > 0) {
                logger.debug { "Rule ${rule.title} (${rule.id}) has already been activated for user ${appUser.userId}" }
                return false
            }
        }
        return true
    }

    private fun validateConditions(
        rule: Rule,
        appUser: AppUser,
        params: Map<String, Any>?,
    ): Boolean {
        if (rule.conditions.isNullOrEmpty()) {
            return params.isNullOrEmpty()
        }

        val conditions = rule.conditions!!
        val conditionsMatch =
            conditions.all { condition ->
                val paramValue = params?.get(condition.key)
                val conditionValue = condition.value

                // TODO: forward-chain: This is a quick work around, utilizing conditions to check against appUser,
                //  which is different from normal flow, that checks trigger params.
                //  Ideally, we should have a separate trigger config field for this?
                val isPointsReached = rule.trigger!!.key == "points_reached"

                val matches =
                    if (isPointsReached) {
                        val pointsThreshold = conditionValue as? Int
                        val appUserPoints = appUser.points
                        logger.warn {
                            "Workaround for points_reached: Checking points: " +
                                "appUserPoints=$appUserPoints, pointsThreshold=$pointsThreshold"
                        }
                        pointsThreshold != null && appUserPoints != null && appUserPoints >= pointsThreshold
                    } else {
                        // TODO: support more operators
                        paramValue == conditionValue
                    }

                if (matches) {
                    logger.debug { "Condition ${condition.key} matches" }
                } else {
                    logger.debug { "Condition ${condition.key} does not match: $paramValue != $conditionValue" }
                }

                matches
            }

        logger.debug { "Rule ${rule.title} (${rule.id}) conditions match: $conditionsMatch" }

        return conditionsMatch
    }
}

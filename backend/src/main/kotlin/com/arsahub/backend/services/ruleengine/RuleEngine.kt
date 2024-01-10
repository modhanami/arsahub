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
import com.arsahub.backend.repositories.RuleRepository
import com.arsahub.backend.repositories.TriggerLogRepository
import com.arsahub.backend.services.TriggerService
import com.arsahub.backend.services.actionhandlers.ActionHandlerRegistry
import com.arsahub.backend.services.actionhandlers.ActionResult
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RuleEngine(
    private val ruleRepository: RuleRepository,
    private val actionHandlerRegistry: ActionHandlerRegistry,
    private val triggerLogRepository: TriggerLogRepository,
    private val ruleProgressRepository: RuleProgressRepository,
    private val triggerService: TriggerService,
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
        val trigger = triggerService.getTriggerOrThrow(request.key, app)

        logTrigger(trigger, app, appUser, rawRequestJson)

        triggerService.validateParamsAgainstTriggerFields(request.params, trigger.fields)

        val matchingRules = getMatchingRules(trigger)
        for (rule in matchingRules) {
            logger.debug { "Checking rule ${rule.title} (${rule.id})" }

            if (
                // check repeatability
                !validateRepeatability(rule, appUser) ||
                // check the params against the rule conditions, if any
                !validateConditions(rule, request.params)
            ) {
                //  not repeatable or conditions don't match
                continue
            }

            // handle action
            val actionResult = activateRule(rule, appUser)

            afterAction(actionResult)
        }

        /*
         TODO: Currently, the trigger log is committed as a whole with other operations.
               Could possibly let Kafka handle this in the future and separate ingestion from processing.
         */
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
        appUser: AppUser,
    ): ActionResult {
        val actionResult = actionHandlerRegistry.handleAction(rule, appUser)

        // update rule progress
        progressRule(rule, appUser)

        return actionResult
    }

    private fun progressRule(
        rule: Rule,
        appUser: AppUser,
    ): RuleProgress {
        val ruleProgress =
            ruleProgressRepository.findByRuleAndAppUser(rule, appUser) ?: RuleProgress(
                rule = rule,
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
        params: Map<String, Any>?,
    ): Boolean {
        if (rule.conditions != null) {
            val conditions = rule.conditions!!
            val conditionsMatch =
                conditions.all { condition ->
                    val paramValue = params?.get(condition.key)
                    val conditionValue = condition.value
                    val matches = paramValue == conditionValue // TODO: support more operators

                    if (matches) {
                        logger.debug { "Condition ${condition.key} matches" }
                    } else {
                        logger.debug { "Condition ${condition.key} does not match: $paramValue != $conditionValue" }
                    }

                    matches
                }

            if (!conditionsMatch) {
                logger.debug { "Rule ${rule.title} (${rule.id}) conditions do not match" }
                return false
            }
        }

        return true
    }

    private fun getMatchingRules(trigger: Trigger): List<Rule> {
        return ruleRepository.findAllByTrigger_Key(trigger.key!!)
    }
}

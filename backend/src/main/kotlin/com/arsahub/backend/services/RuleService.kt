package com.arsahub.backend.services

import com.arsahub.backend.dtos.request.Action
import com.arsahub.backend.dtos.request.ActionDefinition
import com.arsahub.backend.dtos.request.AddPointsAction
import com.arsahub.backend.dtos.request.RuleCreateRequest
import com.arsahub.backend.dtos.request.RuleUpdateRequest
import com.arsahub.backend.dtos.request.UnlockAchievementAction
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.exceptions.NotFoundException
import com.arsahub.backend.models.App
import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.RuleRepeatability
import com.arsahub.backend.models.Trigger
import com.arsahub.backend.repositories.RuleProgressRepository
import com.arsahub.backend.repositories.RuleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

class RuleNotFoundException : NotFoundException("Rule not found")

class RuleInUseException : ConflictException("Rule is in use")

@Service
class RuleService(
    private val achievementService: AchievementService,
    private val triggerService: TriggerService,
    private val ruleRepository: RuleRepository,
    private val ruleProgressRepository: RuleProgressRepository,
) {
    fun listRules(app: App): List<Rule> {
        return ruleRepository.findAllByApp(app)
    }

    fun getMatchingRules(
        app: App,
        trigger: Trigger,
    ): List<Rule> {
        return ruleRepository.findAllByAppAndTrigger_Key(app, trigger.key!!)
    }

    fun createRule(
        app: App,
        request: RuleCreateRequest,
    ): Rule {
        val trigger =
            runCatching {
                triggerService.getTriggerOrThrow(request.trigger.key!!, app)
            }.getOrElse {
                triggerService.getBuiltInTriggerOrThrow(request.trigger.key!!)
            }

        // validate action definition
        val parsedAction = parseActionDefinition(request.action)

        // validate repeatability
        val ruleRepeatability = RuleRepeatability.valueOf(request.repeatability!!)
        validateRepeatabilityForBuiltInTrigger(ruleRepeatability, trigger)

        triggerService.validateParamsAgainstTriggerFields(request.conditions, trigger.fields)

        // TODO: more validations for conditions

        val rule =
            Rule(
                app = app,
                title = request.title,
                description = request.description,
                trigger = trigger,
                triggerParams = request.trigger.params?.toMutableMap(),
                conditions = request.conditions?.toMutableMap(),
                repeatability = ruleRepeatability.key,
            )

        rule.action = parsedAction.key
        when (parsedAction) {
            is AddPointsAction -> {
                require(parsedAction.points > 0) { "Points must be greater than 0" }
                rule.actionPoints = parsedAction.points
            }

            is UnlockAchievementAction -> {
                val achievement = achievementService.getAchievementOrThrow(parsedAction.achievementId, app)
                rule.actionAchievement = achievement
            }
        }

        return ruleRepository.save(rule)
    }

    fun updateRule(
        app: App,
        ruleId: Long,
        request: RuleUpdateRequest,
    ): Rule {
        val rule = ruleRepository.findByIdAndApp(ruleId, app) ?: throw RuleNotFoundException()

        request.title?.also { rule.title = it }
        request.description?.also { rule.description = it }

        return ruleRepository.save(rule)
    }

    class RuleRepeatabilityMustBeOncePerUserException :
        IllegalArgumentException("Repeatability must be once_per_user for this trigger")

    private fun validateRepeatabilityForBuiltInTrigger(
        repeatability: RuleRepeatability,
        trigger: Trigger,
    ) {
        if (trigger.key!! == "points_reached" && RuleRepeatability.ONCE_PER_USER != repeatability.key) {
            throw RuleRepeatabilityMustBeOncePerUserException()
        }
    }

    private fun parseActionDefinition(actionDefinition: ActionDefinition): Action {
        val actionKey = actionDefinition.key
        val params = actionDefinition.params

        requireNotNull(params) { "Params are required for action $actionKey" }

        return when (actionKey) {
            "add_points" -> parseActionAddPointsParams(params)
            "unlock_achievement" -> parseActionUnlockAchievementParams(params)
            else -> throw IllegalArgumentException("Unknown action key: $actionKey")
        }
    }

    private fun parseActionAddPointsParams(params: Map<String, Any>): Action {
        val points = params["points"] as? Int ?: throw IllegalArgumentException("Points is invalid")
        return AddPointsAction(points)
    }

    private fun parseActionUnlockAchievementParams(params: Map<String, Any>): Action {
        return when (val rawAchievementId = params["achievementId"]) {
            is Int -> {
                UnlockAchievementAction(rawAchievementId.toLong())
            }

            is Long -> {
                UnlockAchievementAction(rawAchievementId)
            }

            else -> {
                throw IllegalArgumentException("Achievement ID is invalid")
            }
        }
    }

    fun deleteRule(
        app: App,
        ruleId: Long,
    ) {
        val rule = ruleRepository.findByIdOrNull(ruleId) ?: throw RuleNotFoundException()
        assertCanDeleteRule(app, rule)

        rule.markAsDeleted()

        ruleRepository.save(rule)
    }

    private fun assertCanDeleteRule(
        currentApp: App,
        rule: Rule,
    ) {
        if (rule.app!!.id != currentApp.id) {
            throw RuleNotFoundException()
        }
    }

    fun getRuleOrThrow(
        app: App,
        ruleId: Long,
    ): Rule {
        return ruleRepository.findByIdAndApp(ruleId, app) ?: throw RuleNotFoundException()
    }
}

package com.arsahub.backend.services

import com.arsahub.backend.SocketIOService
import com.arsahub.backend.dtos.request.*
import com.arsahub.backend.dtos.response.AchievementResponse
import com.arsahub.backend.dtos.socketio.AchievementUnlock
import com.arsahub.backend.dtos.socketio.LeaderboardUpdate
import com.arsahub.backend.dtos.socketio.PointsUpdate
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.exceptions.NotFoundException
import com.arsahub.backend.models.*
import com.arsahub.backend.repositories.*
import com.arsahub.backend.services.actionhandlers.ActionHandlerRegistry
import com.arsahub.backend.services.actionhandlers.ActionResult
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*


@Service
class AppService(
    private val triggerRepository: TriggerRepository,
    private val userRepository: UserRepository,
    private val appRepository: AppRepository,
    private val appUserRepository: AppUserRepository,
    private val achievementRepository: AchievementRepository,
    private val socketIOService: SocketIOService,
    private val ruleRepository: RuleRepository,
    private val actionHandlerRegistry: ActionHandlerRegistry,
    private val leaderboardService: LeaderboardService,
    private val triggerLogRepository: TriggerLogRepository,
    private val ruleProgressRepository: RuleProgressRepository,
) {
    private val logger = KotlinLogging.logger {}

    class TriggerNotFoundException(triggerKey: String) : NotFoundException("Trigger with key $triggerKey not found")

    class AppUserNotFoundException(userId: String) : NotFoundException("App user with ID $userId not found")
    class AppUserAlreadyExistsException(userId: String) : ConflictException("App user with ID $userId already exists")


    class AppNotFoundException(appId: Long) : NotFoundException("App with ID $appId not found")
    class AppNotFoundForUserException(uuid: UUID) : Exception("App not found for user with UUID $uuid")

    class UserNotFoundException(uuid: UUID) : NotFoundException("User with UUID $uuid not found")

    fun createTrigger(app: App, request: TriggerCreateRequest): Trigger {
        val existingApp = getAppOrThrow(app.id!!)

        logger.debug { "Received trigger create request for app ${existingApp.title} (${existingApp.id}): Name = ${request.title}, Key = ${request.key}" }

        // validate field definitions
        if (request.fields != null) {
            logger.debug { "Validating ${request.fields.size} field definitions" }
            request.fields.forEach { field ->
                field.type?.let { require(TriggerFieldType.supports(it)) { "Invalid field type: ${field.type}" } }
            }
        }

        // save trigger and fields
        val trigger = Trigger(
            title = request.title,
            description = request.description,
            key = request.key,
            app = existingApp,
        )
        val triggerFields = request.fields?.map { field ->
            TriggerField(
                key = field.key,
                type = field.type!!,
                label = field.label,
                trigger = trigger
            )
        }?.toMutableSet() ?: emptySet()

        trigger.fields = triggerFields.toMutableSet()

        val savedTrigger = triggerRepository.save(trigger)

        logger.info { "Trigger ${savedTrigger.title} (${savedTrigger.id}) created for app ${existingApp.title} (${existingApp.id})" }

        return savedTrigger
    }

    fun getAppOrThrow(id: Long): App {
        return appRepository.findById(id).orElseThrow { AppNotFoundException(id) }
    }

    fun getTriggers(app: App): List<Trigger> {
        return triggerRepository.findAllByAppId(app.id!!)
    }

    fun getAppByUserUUID(uuid: UUID): App {
        return appRepository.findFirstByOwnerUuid(uuid) ?: throw AppNotFoundForUserException(uuid)
    }

    fun getUserByUUID(userUUID: UUID): User {
        return userRepository.findByUuid(userUUID) ?: throw UserNotFoundException(userUUID)
    }

    fun addUser(app: App, request: AppUserCreateRequest): AppUser {
        val appUser = appUserRepository.findByAppAndUserId(app, request.uniqueId)
        if (appUser != null) {
            throw AppUserAlreadyExistsException(request.uniqueId)
        }
        val newAppUser = AppUser(
            userId = request.uniqueId,
            displayName = request.displayName,
            app = app
        )
        appUserRepository.save(newAppUser)
        return newAppUser
    }

    fun listUsers(app: App): List<AppUser> {
        return appUserRepository.findAllByApp(app)
    }

    fun getAppUserOrThrow(app: App, userId: String): AppUser {
        return appUserRepository.findByAppAndUserId(app, userId) ?: throw AppUserNotFoundException(userId)
    }

    fun trigger(app: App, request: TriggerSendRequest, rawRequestJson: Map<String, Any>) {
        val userId = request.userId
        val trigger = getTriggerOrThrow(request.key, app)
        val appUser = getAppUserOrThrow(app, userId)

        // save trigger log before any validation
        logTrigger(trigger, app, appUser, rawRequestJson)

        validateParamsAgainstTriggerFields(request.params, trigger.fields)

        val matchingRules = getMatchingRules(trigger)
        for (rule in matchingRules) {
            logger.debug { "Checking rule ${rule.title} (${rule.id})" }

            // check repeatability
            if (!validateRepeatability(rule, appUser)) {
                continue
            }

            // check the params against the rule conditions, if any
            if (!validateConditions(rule, request.params)) {
                continue
            }

            // handle action
            val actionResult = activateRule(rule, appUser)

            broadcastActionResult(actionResult, app, userId)
        }

    }

    fun logTrigger(trigger: Trigger, app: App, appUser: AppUser, rawRequestJson: Map<String, Any>) {
        val triggerLog = TriggerLog(
            trigger = trigger,
            requestBody = rawRequestJson.toMutableMap(),
            app = app,
            appUser = appUser
        )
        triggerLogRepository.save(triggerLog)
        logger.debug { "Received trigger ${trigger.title} (${trigger.id}) for user ${appUser.userId} (${appUser.id}) from app ${app.title} (${app.id})" }
    }

    private fun getMatchingRules(trigger: Trigger): List<Rule> {
        return ruleRepository.findAllByTrigger_Key(trigger.key!!)
    }

    private fun validateRepeatability(rule: Rule, appUser: AppUser): Boolean {
        if (rule.repeatability == RuleRepeatability.ONCE_PER_USER) {
            val ruleProgress = ruleProgressRepository.findByRuleAndAppUser(rule, appUser)
            if (ruleProgress != null && ruleProgress.activationCount!! > 0) {
                logger.debug { "Rule ${rule.title} (${rule.id}) has already been activated for user ${appUser.userId}" }
                return false
            }
        }
        return true
    }

    private fun validateConditions(rule: Rule, params: Map<String, Any>?): Boolean {
        if (rule.conditions != null) {
            val conditions = rule.conditions!!
            val conditionsMatch = conditions.all { condition ->
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

    private fun activateRule(rule: Rule, appUser: AppUser): ActionResult {
        val actionResult = actionHandlerRegistry.handleAction(rule, appUser)

        // update rule progress
        progressRule(rule, appUser)

        return actionResult
    }

    private fun progressRule(rule: Rule, appUser: AppUser): RuleProgress {
        val ruleProgress = ruleProgressRepository.findByRuleAndAppUser(rule, appUser) ?: RuleProgress(
            rule = rule,
            appUser = appUser,
        )
        ruleProgress.activationCount = (ruleProgress.activationCount ?: 0) + 1
        return ruleProgressRepository.save(ruleProgress)
    }

    private fun broadcastActionResult(actionResult: ActionResult, app: App, userId: String) {
        when (actionResult) {
            is ActionResult.AchievementUpdate -> {
                val (achievement) = actionResult
                socketIOService.broadcastToAppRoom(
                    app,
                    AchievementUnlock(
                        userId = userId,
                        achievement = AchievementResponse.fromEntity(
                            achievement
                        )
                    )
                )
                socketIOService.broadcastToUserRoom(
                    userId,
                    AchievementUnlock(
                        userId = userId,
                        achievement = AchievementResponse.fromEntity(
                            achievement
                        )
                    )
                )
            }

            is ActionResult.PointsUpdate -> {
                val (_, newPoints, _) = actionResult
                socketIOService.broadcastToAppRoom(
                    app,
                    PointsUpdate(
                        userId = userId,
                        points = newPoints
                    )
                )
                socketIOService.broadcastToAppRoom(
                    app,
                    LeaderboardUpdate(
                        leaderboard = leaderboardService.getTotalPointsLeaderboard(app.id!!),
                    )
                )

                socketIOService.broadcastToUserRoom(
                    userId,
                    PointsUpdate(
                        userId = userId,
                        points = newPoints
                    )
                )
            }

            is ActionResult.Nothing -> {}
        }

    }

    private fun validateParamsAgainstTriggerFields(params: Map<String, Any>?, fields: Iterable<TriggerField>) {
        params?.keys?.forEach { paramKey ->
            val field = fields.find { it.key == paramKey } ?: return@forEach
            val fieldValue = params[paramKey]

            val fieldType = TriggerFieldType.fromString(field.type!!)

            requireNotNull(fieldType) {
                val message = "Field ${field.key} has an invalid type: ${field.type}"
                logger.error { message }
                message
            }

            when (fieldType) {
                TriggerFieldType.INTEGER -> require(fieldValue is Int) { "Field ${field.key} is not an integer, got $fieldValue" }
                TriggerFieldType.TEXT -> require(fieldValue is String) { "Field ${field.key} is not a text, got $fieldValue" }
            }
        }
    }

    fun getTriggerOrThrow(key: String, app: App): Trigger {
        return triggerRepository.findByKeyAndApp(key, app) ?: throw TriggerNotFoundException(key)
    }

    fun createRule(
        app: App, request: RuleCreateRequest
    ): Rule {
        val trigger = getTriggerOrThrow(request.trigger.key, app)

        // validate action definition
        val parsedAction = parseActionDefinition(request.action)

        // validate repeatability
        val ruleRepeatability = RuleRepeatability.valueOf(request.repeatability)

        validateParamsAgainstTriggerFields(request.conditions, trigger.fields)

        // TODO: more validations for conditions

        val rule = Rule(
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
                val achievement = achievementRepository.findById(parsedAction.achievementId)
                if (achievement.isEmpty) {
                    throw IllegalArgumentException("Achievement not found")
                }
                rule.actionAchievement = achievement.get()
            }
        }

        return ruleRepository.save(rule)
    }

    private fun parseActionDefinition(actionDefinition: ActionDefinition): Action {
        val actionKey = actionDefinition.key
        val params = actionDefinition.params
        return when (actionKey) {
            "add_points" -> {
                val rawPoints = params?.get("points")
                val points = rawPoints?.toIntOrNull()
                if (points == null || points <= 0) {
                    throw IllegalArgumentException("Points is invalid")
                }
                AddPointsAction(points)
            }

            "unlock_achievement" -> {
                val rawAchievementId = params?.get("achievementId")
                val achievementId = rawAchievementId?.toLongOrNull()
                if (achievementId == null || achievementId <= 0) {
                    throw IllegalArgumentException("Achievement ID is invalid")
                }
                UnlockAchievementAction(achievementId)
            }

            else -> throw IllegalArgumentException("Unknown action key: $actionKey")
        }
    }

    fun createAchievement(
        app: App,
        request: AchievementCreateRequest
    ): Achievement {
        val achievement = Achievement(
            title = request.title!!,
            description = request.description,
            app = app
        )

        achievementRepository.save(achievement)

        return achievement
    }

    fun listAchievements(app: App): List<Achievement> {
        return achievementRepository.findAllByApp(app)
    }

    fun listRules(app: App): List<Rule> {
        return ruleRepository.findAllByApp(app)
    }
}


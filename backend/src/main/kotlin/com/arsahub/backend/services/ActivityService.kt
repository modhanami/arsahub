package com.arsahub.backend.services

import com.arsahub.backend.SocketIOService
import com.arsahub.backend.dtos.*
import com.arsahub.backend.models.*
import com.arsahub.backend.repositories.*
import com.arsahub.backend.services.actionhandlers.ActionHandlerRegistry
import com.arsahub.backend.services.actionhandlers.ActionResult
import com.arsahub.backend.utils.JsonSchemaValidationResult
import com.arsahub.backend.utils.JsonSchemaValidator
import com.networknt.schema.SchemaValidatorsConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import java.time.Instant

interface ActivityService {
    fun createActivity(app: App, activityCreateRequest: ActivityCreateRequest): Activity
    fun updateActivity(
        activityId: Long,
        activityUpdateRequest: ActivityUpdateRequest
    ): ActivityResponse

    fun getActivity(activityId: Long): Activity?
    fun addMembers(activityId: Long, request: ActivityAddMembersRequest): ActivityAddMembersResult
    fun listMembers(activityId: Long): List<AppUserActivity>
    fun listActivities(appId: Long): List<Activity>
    fun trigger(activityId: Long, request: ActivityTriggerRequest, json: Map<String, Any>? = null)
    fun createRule(
        activityId: Long, request: RuleCreateRequest
    ): Rule

    fun createAchievement(
        @PathVariable activityId: Long,
        @Valid @RequestBody request: AchievementCreateRequest
    ): Achievement

    fun listAchievements(activityId: Long): List<Achievement>
}

private val logger = KotlinLogging.logger {}

@Service
class ActivityServiceImpl(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository,
    private val userActivityRepository: UserActivityRepository,
    private val triggerRepository: TriggerRepository,
    private val achievementRepository: AchievementRepository,
    private val socketIOService: SocketIOService,
    private val ruleProgressTimeRepository: RuleProgressTimeRepository,
    private val actionRepository: ActionRepository,
    private val ruleRepository: RuleRepository,
    private val actionHandlerRegistry: ActionHandlerRegistry,
    private val appRepository: AppRepository,
    private val appUserRepository: AppUserRepository,
    private val leaderboardServiceImpl: LeaderboardServiceImpl,
    private val customUnitRepository: CustomUnitRepository,
    private val userActivityProgressRepository: UserActivityProgressRepository,
    private val triggerLogRepository: TriggerLogRepository,
    private val jsonSchemaValidator: JsonSchemaValidator

) : ActivityService {

    override fun createActivity(app: App, activityCreateRequest: ActivityCreateRequest): Activity {
        val existingApp = appRepository.findById(app.id!!)
            .orElseThrow { Exception("App not found") }
        val activityToSave = Activity(
            title = activityCreateRequest.title!!,
            description = activityCreateRequest.description,
            app = existingApp
        )
        return activityRepository.save(activityToSave)
    }

    override fun updateActivity(activityId: Long, activityUpdateRequest: ActivityUpdateRequest): ActivityResponse {
        val existingActivity = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")

        existingActivity.title = activityUpdateRequest.title!!
        existingActivity.description = activityUpdateRequest.description

        val updatedActivity = activityRepository.save(existingActivity)
        return ActivityResponse.fromEntity(updatedActivity)
    }

    override fun getActivity(activityId: Long): Activity? {
        return activityRepository.findByIdOrNull(activityId)
    }

    override fun addMembers(
        activityId: Long, request: ActivityAddMembersRequest
    ): ActivityAddMembersResult {
        val existingActivity = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")

        val existingMemberUserIds = existingActivity.members.map { it.appUser?.userId }.toSet()
        val newMemberUserIds = request.userIds.filter { !existingMemberUserIds.contains(it) }
        val newMemberAppUsers = appUserRepository.findAllByAppAndUserIdIn(existingActivity.app!!, newMemberUserIds)
        val newMembers = newMemberAppUsers.map { appUser ->
            AppUserActivity(
                appUser = appUser,
                activity = existingActivity,
            )
        }
        userActivityRepository.saveAllAndFlush(newMembers)

        return ActivityAddMembersResult(
            activity = existingActivity,
            hasNewMembers = newMembers.isNotEmpty()
        )
    }

    override fun listMembers(activityId: Long): List<AppUserActivity> {
        val existingEvent = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")

        return existingEvent.members.toList()
    }

    override fun listActivities(appId: Long): List<Activity> {
        return activityRepository.findAllByAppId(appId)
    }

    override fun trigger(activityId: Long, request: ActivityTriggerRequest, json: Map<String, Any>?) {
        val existingActivity = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")
        val userId = request.userId

        val member =
            existingActivity.members.find { it.appUser?.userId == userId } ?: throw Exception("User not found")

        val trigger = triggerRepository.findByKey(request.key) ?: throw Exception("Trigger not found")

        // save trigger log before any validation
        val triggerLog = TriggerLog(
            trigger = trigger,
            requestBody = json?.toMutableMap(),
            app = existingActivity.app,
            appUser = member.appUser
        )
        triggerLogRepository.save(triggerLog)

        // validate trigger against schema
        val triggerSchema = trigger.jsonSchema
        val triggerValidationResult = if (triggerSchema != null) {
            if (request.params.isNullOrEmpty()) {
                throw Exception("Trigger params must be provided when trigger has a schema (key: ${trigger.key})")
            }

            jsonSchemaValidator.validate(triggerSchema, request.params)
        } else {
            JsonSchemaValidationResult.valid()
        }

        logger.debug { "Trigger validation result: ${if (triggerValidationResult.isValid) "passed" else "failed"} ${triggerValidationResult.errors}" }
        if (!triggerValidationResult.isValid) {
            throw Exception("Trigger definition is not valid (${triggerValidationResult.errors})")
        }

        existingActivity.rules.filter { it.trigger?.key == trigger.key }.forEach { rule -> // TODO: fetch from DB?
            val triggerType: TriggerType? = rule.triggerType
            if (triggerType == null) {
                println("Trigger type not found for rule ${rule.title} (${rule.id}), repeating")
            }


//            // handle pre-built trigger types and rule user progress
//            var isProgressCompleted = false
//            when (triggerType?.key) {
//                "times" -> {
//                    val expectedCount =
//                        rule.triggerTypeParams?.get("count")?.toString()?.toInt() ?: throw Exception("Count not found")
//                    var ruleProgress = ruleProgressTimeRepository.findByRuleAndUserActivity(rule, existingMember)
//                    if (ruleProgress == null) {
//                        ruleProgress = RuleProgressTime(
//                            rule = rule, appUserActivity = existingMember, progress = 0
//                        )
//                    } else {
//                        if (ruleProgress.completedAt != null) {
//                            return@forEach
//                        }
//                    }
//
//                    ruleProgress.progress = ruleProgress.progress?.plus(1)
//                    if (ruleProgress.progress == expectedCount) {
//                        ruleProgress.completedAt = Instant.now()
//                        isProgressCompleted = true
//                        println("User ${existingMember.user?.userId} (${existingMember.user?.userId}) has completed rule ${rule.title} (${rule.id}) with progress ${ruleProgress.progress}/${expectedCount}")
//                    } else {
//                        println("User ${existingMember.user?.userId} (${existingMember.user?.userId}) has progress ${ruleProgress.progress}/${expectedCount} for rule ${rule.title} (${rule.id})")
//                    }
//
//                    ruleProgressTimeRepository.save(ruleProgress)
//                }
//            }
//
//            if (triggerType != null && !isProgressCompleted) {
//                return@forEach
//            }

            when (val actionResult = actionHandlerRegistry.handleAction(rule, member)) {
                is ActionResult.AchievementUpdate -> {
                    val (achievement) = actionResult
                    socketIOService.broadcastToActivityRoom(
                        activityId,
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
                    socketIOService.broadcastToActivityRoom(
                        activityId,
                        PointsUpdate(
                            userId = userId,
                            points = newPoints
                        )
                    )
                    socketIOService.broadcastToActivityRoom(
                        activityId,
                        LeaderboardUpdate(
                            leaderboard = leaderboardServiceImpl.getTotalPointsLeaderboard(activityId)
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

        // find all rules with points_reached as a trigger to activate the corresponding actions
        if (request.key != "points_reached") {
            existingActivity.rules.filter { it.trigger?.key == "points_reached" }.forEach { rule ->
                val value = rule.triggerParams?.get("value")?.toString()?.toInt()
                    ?: throw Exception("Value not found for rule ${rule.title} (${rule.id})")

                if ((member.points ?: 0) < value) {
                    return@forEach
                }

                // check if the rule has already been activated from rule_progress_time
                if (ruleProgressTimeRepository.findByRuleAndAppUserActivity(rule, member) != null) {
                    return@forEach
                }

                println("User reached ${member.points} points, activating rule ${rule.title} (${rule.id})")

                trigger(
                    activityId,
                    ActivityTriggerRequest(
                        key = "points_reached",
                        params = emptyMap(),
                        userId = userId
                    )
                )

                // mark the rule as activated for the user
                val ruleProgress = RuleProgressTime(
                    rule = rule, appUserActivity = member, progress = 1, completedAt = Instant.now()
                )
                ruleProgressTimeRepository.save(ruleProgress)
            }
        }

        // handle custom units, extracting values from params based on the unit's key
        // - unit of type "Integer" => Increment value to unit's progress
        // - unit of type "Integer Array" => Add new value to unit's progress array
//        if (!request.params.isNullOrEmpty()) {
//            for (joinedCustomUnit in trigger.customUnits) {
//                val customUnit = joinedCustomUnit.customUnit!!
//                val unitKey = customUnit.key
//                // if the current trigger is also the custom unit related trigger, skip it
//                if (request.key.endsWith("_reached")) {
//                    continue
//                }
//
//                val unitType = customUnit.type?.name
//                val unitValue = request.params[unitKey]
//
//                if (unitValue == null) {
//                    logger.debug { "Unit value not found for key $unitKey, skipping" }
//                    continue
//                }
//
//                val userActivityProgress = userActivityProgressRepository.findByAppUserActivityAndCustomUnit(
//                    member, customUnit
//                ) ?: UserActivityProgress(
//                    appUserActivity = member, customUnit = customUnit
//                )
//
//                val valueInt = unitValue.toInt()
//                when (unitType) {
//                    "Integer" -> {
//                        val currentValue = userActivityProgress.valueInt ?: 0
//                        val newValue = currentValue + valueInt
//                        userActivityProgress.valueInt = newValue
//                        userActivityProgressRepository.save(userActivityProgress)
//                        logger.debug { "User ${member.appUser?.displayName} (${member.appUser?.userId}) progress 'Integer' $valueInt for unit $unitKey (${customUnit.id}) in activity $activityId" }
//                    }
//
//                    "Integer Set" -> {
//                        val currentValueSet = (userActivityProgress.valueIntArray ?: mutableListOf()).toMutableSet()
//                        if (currentValueSet.add(valueInt)) {
//                            userActivityProgress.valueIntArray = currentValueSet.toMutableList()
//                            userActivityProgressRepository.save(userActivityProgress)
//                            logger.debug { "User ${member.appUser?.displayName} (${member.appUser?.userId}) progress 'Integer Set' $valueInt for unit $unitKey (${customUnit.id}) in activity $activityId" }
//                        } else {
//                            logger.debug { "Skipping. User ${member.appUser?.displayName} (${member.appUser?.userId}) already has progress 'Integer Set' $valueInt for unit $unitKey (${customUnit.id}) in activity $activityId" }
//                        }
//                    }
//                }
//
//                // activate rules with custom unit triggers
//                val unitReachedRules =
//                    existingActivity.rules.filter { it.trigger?.key == "${unitKey}_reached" } // TODO: fetch from DB?
//                if (unitReachedRules.isEmpty()) {
//                    logger.debug { "No chain rules found for unit $unitKey (${customUnit.id}) in activity $activityId" }
//                    continue
//                } else {
//                    logger.debug { "Found ${unitReachedRules.size} chain rules for unit $unitKey (${customUnit.id}) in activity $activityId, activating" }
//                }
//                for (rule in unitReachedRules) {
//                    val triggerParams = rule.triggerParams
//                    val triggerValue = triggerParams?.get("value")?.toString()?.toInt()
//                        ?: throw Exception("Value not found for rule ${rule.title} (${rule.id})")
//
//                    if (ruleProgressTimeRepository.findByRuleAndAppUserActivity(rule, member) != null) {
//                        logger.debug { "User ${member.appUser?.displayName} (${member.appUser?.userId}) has already reached $triggerValue for unit $unitKey (${customUnit.id}) in activity $activityId, skipping rule ${rule.title} (${rule.id})" }
//                        continue
//                    }
//
//                    if ((userActivityProgress.valueInt ?: 0) >= triggerValue) {
//                        logger.debug { "User ${member.appUser?.displayName} (${member.appUser?.userId}) has reached $triggerValue for unit $unitKey (${customUnit.id}) in activity $activityId, activating rule ${rule.title} (${rule.id})" }
//                        trigger(
//                            activityId,
//                            ActivityTriggerRequest(
//                                key = "${unitKey}_reached",
//                                params = triggerParams.mapValues { it.value.toString() },
//                                userId = userId
//                            )
//                        )
//
//                        // mark the rule as activated for the user
//                        val ruleProgress = RuleProgressTime(
//                            rule = rule, appUserActivity = member, progress = 1, completedAt = Instant.now()
//                        )
//                        ruleProgressTimeRepository.save(ruleProgress)
//
//                    } else {
//                        logger.debug { "User ${member.appUser?.displayName} (${member.appUser?.userId}) has not reached $triggerValue for unit $unitKey (${customUnit.id}) in activity $activityId, skipping rule ${rule.title} (${rule.id})" }
//                    }
//                }
//            }
//        }

    }

    override fun createRule(
        activityId: Long, request: RuleCreateRequest
    ): Rule {
        val trigger = triggerRepository.findByKey(request.trigger.key) ?: throw Exception("Trigger not found")
        val action = actionRepository.findByKey(request.action.key) ?: throw Exception("Action not found")
//        val triggerType = request.condition?.type?.let { triggerTypeRepository.findByKey(it) }
        val triggerType = null

        val activity = getActivity(activityId) ?: throw Exception("Activity not found")


        val schemaValidatorsConfig = SchemaValidatorsConfig()
        schemaValidatorsConfig.isTypeLoose = true

        // trigger schema validation
        println("Trigger definition: ${request.trigger}")
        println("Trigger schema: ${trigger.jsonSchema}")
        val triggerSchema = trigger.jsonSchema
        val triggerValidationResult = if (triggerSchema != null) {
            if (request.trigger.params == null) {
                throw Exception("Trigger params must be provided when trigger has a schema (key: ${trigger.key})")
            }

            jsonSchemaValidator.validate(triggerSchema, request.trigger.params)
        } else {
            JsonSchemaValidationResult.valid()
        }

        println("Trigger validation result: ${triggerValidationResult.errors} (passed: ${triggerValidationResult.isValid})")
        if (!triggerValidationResult.isValid) {
            throw Exception("Trigger definition is not valid")
        }

        // action schema validation
        println("Action definition: ${request.action}")
        println("Action schema: ${action.jsonSchema}")
        val actionSchema = action.jsonSchema
        val actionValidationResult = if (actionSchema != null) {
            jsonSchemaValidator.validate(actionSchema, request.action.params)
        } else {
            JsonSchemaValidationResult.valid()
        }

        println("Action validation result: ${actionValidationResult.errors} (passed: $actionValidationResult.isValid)")
        if (!actionValidationResult.isValid) {
            throw Exception("Action definition is not valid")
        }

        // extra validation not covered by schema, like a param must reference a valid ID of an achievement to be awarded (achievementId)
        // checks like nullability, type, etc. are covered by the schema
        try {
            when (request.action.key) {
                "unlock_achievement" -> {
                    val achievementId = request.action.params["achievementId"]!!.toLong()
                    val achievement = achievementRepository.findById(achievementId)
                    if (achievement.isEmpty) {
                        throw Exception("Achievement not found")
                    }
                }
            }
        } catch (e: Exception) {
            throw Exception("Action definition is not valid", e)
        }


//        if (triggerType != null) {
//            // validate condition schema
//            println("Condition definition: ${request.condition}")
//            println("Condition schema: ${triggerType.jsonSchema}")
//            val conditionSchema = triggerType.jsonSchema
//            val conditionValidate = if (conditionSchema != null) {
//                jsonSchemaValidator.validate(conditionSchema, request.condition.params)
//            } else {
//                JsonSchemaValidationResult.valid()
//            }
//
//            println("Condition validation result: ${conditionValidate.errors} (passed: ${conditionValidate.isValid})")
//            if (!conditionValidate.isValid) {
//                throw Exception("Condition definition is not valid")
//            }
//        }

        val rule = Rule(
            title = request.name,
            description = request.description,
            trigger = trigger,
            action = action,
            triggerType = triggerType,
            activity = activity,
            triggerParams = request.trigger.params?.toMutableMap(),
            actionParams = request.action.params.toMutableMap(),
            triggerTypeParams = request.condition?.params?.toMutableMap(),
        )

        return ruleRepository.save(rule)
    }

    override fun createAchievement(
        activityId: Long,
        request: AchievementCreateRequest
    ): Achievement {
        val activity = getActivity(activityId) ?: throw Exception("Activity not found")
        val achievement = Achievement(
            title = request.title!!,
            description = request.description,
            activity = activity
        )

        achievementRepository.save(achievement)

        return achievement
    }

    override fun listAchievements(activityId: Long): List<Achievement> {
        val activity = getActivity(activityId) ?: throw Exception("Activity not found")
        return achievementRepository.findAllByActivity(activity)
    }
}

package com.arsahub.backend.services

import com.arsahub.backend.SocketIOService
import com.arsahub.backend.controllers.ActivityController
import com.arsahub.backend.dtos.*
import com.arsahub.backend.models.*
import com.arsahub.backend.repositories.*
import com.arsahub.backend.utils.JsonSchemaValidationResult
import com.arsahub.backend.utils.JsonSchemaValidator
import com.networknt.schema.SchemaValidatorsConfig
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant

interface ActivityService {
    fun createActivity(activityCreateRequest: ActivityCreateRequest): Activity
    fun updateActivity(
        activityId: Long,
        activityUpdateRequest: ActivityUpdateRequest
    ): ActivityResponse

    fun getActivity(activityId: Long): Activity?
    fun addMembers(activityId: Long, request: ActivityController.ActivityAddMembersRequest): ActivityResponse
    fun listMembers(activityId: Long): List<UserActivity>
    fun listActivities(): List<ActivityResponse>
    fun trigger(activityId: Long, request: ActivityTriggerRequest)
    fun createRule(
        activityId: Long, request: ActivityController.RuleCreateRequest
    ): Rule
}

@Service
class ActivityServiceImpl(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository,
    private val userActivityRepository: UserActivityRepository,
    private val triggerRepository: TriggerRepository,
    private val achievementRepository: AchievementRepository,
    private val userActivityAchievementRepository: UserActivityAchievementRepository,
    private val socketIOService: SocketIOService,
    private val ruleProgressTimeRepository: RuleProgressTimeRepository,
    private val actionRepository: ActionRepository,
    private val triggerTypeRepository: TriggerTypeRepository,
    private val ruleRepository: RuleRepository
) : ActivityService {

    override fun createActivity(activityCreateRequest: ActivityCreateRequest): Activity {
        val activityToSave = Activity(
            title = activityCreateRequest.title!!,
            description = activityCreateRequest.description,
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
        activityId: Long, request: ActivityController.ActivityAddMembersRequest
    ): ActivityResponse {
        val existingEvent = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")

        val existingMemberUserIds = existingEvent.members.map { it.user?.externalUserId }.toSet()
        val newMemberUserIds = request.externalUserIds.filter { !existingMemberUserIds.contains(it) }
        val newMemberUsers = userRepository.findAll().filter { newMemberUserIds.contains(it.externalUserId) }
        newMemberUsers.forEach { println(it.externalUserId) }
        val newMembers = newMemberUsers.map { user ->
            UserActivity(
                user = user,
                activity = existingEvent,
            )
        }
        userActivityRepository.saveAllAndFlush(newMembers)

        return ActivityResponse.fromEntity(
            activityRepository.findByIdOrNull(activityId)
                ?: throw EntityNotFoundException("Activity with ID $activityId not found")
        )
    }

    override fun listMembers(activityId: Long): List<UserActivity> {
        val existingEvent = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")

        return existingEvent.members.toList()
    }

    override fun listActivities(): List<ActivityResponse> {
        return activityRepository.findAll().map { ActivityResponse.fromEntity(it) }
    }

    override fun trigger(activityId: Long, request: ActivityTriggerRequest) {
        val allActivity = activityRepository.findAll()
        println("${allActivity.size} activities found")
        val existingActivity = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")

        val userId = request.userId

        val existingMember =
            existingActivity.members.find { it.user?.externalUserId == userId } ?: throw Exception("User not found")

        val trigger = triggerRepository.findByKey(request.key) ?: throw Exception("Trigger not found")

        var isPointsUpdated = false
        var unlockedAchievement: Achievement? = null

        val actionMessages = mutableListOf<String>()
        existingActivity.rules.filter { it.trigger?.key == trigger.key }.forEach { rule ->
            val action = rule.action ?: throw Exception("Action not found")

            val triggerType: TriggerType? = rule.triggerType
            if (triggerType == null) {
                println("Trigger type not found for rule ${rule.title} (${rule.id}), repeating")
            }

            // handle pre-built trigger types and rule user progress
            var isProgressCompleted = false
            when (triggerType?.key) {
                "times" -> {
                    val expectedCount =
                        rule.triggerTypeParams?.get("count")?.toString()?.toInt() ?: throw Exception("Count not found")
                    var ruleProgress = ruleProgressTimeRepository.findByRuleAndUserActivity(rule, existingMember)
                    if (ruleProgress == null) {
                        ruleProgress = RuleProgressTime(
                            rule = rule, userActivity = existingMember, progress = 0
                        )
                    } else {
                        if (ruleProgress.completedAt != null) {
                            return@forEach
                        }
                    }

                    ruleProgress.progress = ruleProgress.progress?.plus(1)
                    if (ruleProgress.progress == expectedCount) {
                        ruleProgress.completedAt = Instant.now()
                        isProgressCompleted = true
                        println("User ${existingMember.user?.userId} (${existingMember.user?.externalUserId}) has completed rule ${rule.title} (${rule.id}) with progress ${ruleProgress.progress}/${expectedCount}")
                    } else {
                        println("User ${existingMember.user?.userId} (${existingMember.user?.externalUserId}) has progress ${ruleProgress.progress}/${expectedCount} for rule ${rule.title} (${rule.id})")
                    }

                    ruleProgressTimeRepository.save(ruleProgress)
                }
            }

            if (triggerType != null && !isProgressCompleted) {
                return@forEach
            }

            // handle pre-built actions
            when (action.key) {
                "add_points" -> {
                    val points =
                        rule.actionParams?.get("value")?.toString()?.toInt() ?: throw Exception("Points not found")
                    existingMember.addPoints(points)
                    userActivityRepository.save(existingMember)
                    isPointsUpdated = true

                    actionMessages.add("User `${existingMember.user?.userId}` (${existingMember.user?.externalUserId}) received `$points` points for activity `${existingActivity.title}` (${existingActivity.activityId}) from rule `${rule.title}` (${rule.id})")
                }

                "unlock_achievement" -> {
                    val achievementId =
                        rule.actionParams?.get("achievementId")?.toString()?.toLong()
                            ?: throw Exception("Achievement ID not found")
                    val achievement =
                        achievementRepository.findByIdOrNull(achievementId) ?: throw Exception("Achievement not found")

                    // precondition: user must not have unlocked the achievement
                    if (existingMember.userActivityAchievements.any { it.achievement?.achievementId == achievementId }) {
                        actionMessages.add("User already unlocked achievement")
                        return@forEach
                    }

                    existingMember.addAchievement(achievement)
                    // save from the owning side
                    userActivityAchievementRepository.saveAll(existingMember.userActivityAchievements)
                    unlockedAchievement = achievement

                    actionMessages.add("User `${existingMember.user?.userId}` (${existingMember.user?.externalUserId}) unlocked achievement `${achievement.title}` (${achievement.achievementId}) for activity `${existingActivity.title}` (${existingActivity.activityId}) from rule `${rule.title}` (${rule.id})")
                }
            }
        }

        if (isPointsUpdated) {
            socketIOService.broadcastToActivityRoom(
                activityId,
                PointsUpdate(
                    userId = userId,
                    points = existingMember.points ?: 0
                )
            )
            socketIOService.broadcastToUserRoom(
                userId,
                PointsUpdate(
                    userId = userId,
                    points = existingMember.points ?: 0
                )
            )
        }

        unlockedAchievement?.let { achievement ->
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

        // find all rules with points_reached as a trigger to activate the corresponding actions
        if (request.key != "points_reached") {
            existingActivity.rules.filter { it.trigger?.key == "points_reached" }.forEach { rule ->
                val value = rule.triggerParams?.get("value")?.toString()?.toInt()
                    ?: throw Exception("Value not found for rule ${rule.title} (${rule.id})")

                if ((existingMember.points ?: 0) < value) {
                    return@forEach
                }

                // check if the rule has already been activated from rule_progress_time
                if (ruleProgressTimeRepository.findByRuleAndUserActivity(rule, existingMember) != null) {
                    return@forEach
                }

                println("User reached ${existingMember.points} points, activating rule ${rule.title} (${rule.id})")

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
                    rule = rule, userActivity = existingMember, progress = 1, completedAt = Instant.now()
                )
                ruleProgressTimeRepository.save(ruleProgress)
            }
        }

        actionMessages.forEach { println(it) }
    }

    override fun createRule(
        activityId: Long, request: ActivityController.RuleCreateRequest
    ): Rule {
        val trigger = triggerRepository.findByKey(request.trigger.key) ?: throw Exception("Trigger not found")
        val action = actionRepository.findByKey(request.action.key) ?: throw Exception("Action not found")
        val triggerType =
            request.condition?.type?.let { triggerTypeRepository.findByKey(it) }

        val activity = getActivity(activityId) ?: throw Exception("Activity not found")


        val schemaValidatorsConfig = SchemaValidatorsConfig()
        schemaValidatorsConfig.isTypeLoose = true
        val validator = JsonSchemaValidator(schemaValidatorsConfig = schemaValidatorsConfig)

        // trigger schema validation
        println("Trigger definition: ${request.trigger}")
        println("Trigger schema: ${trigger.jsonSchema}")
        val triggerSchema = trigger.jsonSchema
        val triggerValidationResult = if (triggerSchema != null) {
            if (request.trigger.params == null) {
                throw Exception("Trigger params must be provided when trigger has a schema (key: ${trigger.key})")
            }

            validator.validate(triggerSchema, request.trigger.params)
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
            validator.validate(actionSchema, request.action.params)
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


        if (triggerType != null) {
            // validate condition schema
            println("Condition definition: ${request.condition}")
            println("Condition schema: ${triggerType.jsonSchema}")
            val conditionSchema = triggerType.jsonSchema
            val conditionValidate = if (conditionSchema != null) {
                validator.validate(conditionSchema, request.condition.params)
            } else {
                JsonSchemaValidationResult.valid()
            }

            println("Condition validation result: ${conditionValidate.errors} (passed: ${conditionValidate.isValid})")
            if (!conditionValidate.isValid) {
                throw Exception("Condition definition is not valid")
            }
        }

        val rule = Rule(
            title = request.title,
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
}

package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.*
import com.arsahub.backend.models.Achievement
import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.Trigger
import com.arsahub.backend.models.UserActivity
import com.arsahub.backend.repositories.*
import com.arsahub.backend.services.ActivityService
import com.arsahub.backend.services.LeaderboardService
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SchemaValidatorsConfig
import com.networknt.schema.SpecVersionDetector
import com.networknt.schema.ValidationMessage
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/activities")
class ActivityController(
    private val activityService: ActivityService,
    private val activityRepository: ActivityRepository,
    private val leaderboardService: LeaderboardService,
    private val ruleRepository: RuleRepository,
    private val triggerRepository: TriggerRepository,
    private val triggerTypeRepository: TriggerTypeRepository,
    private val actionRepository: ActionRepository,
    private val achievementRepository: AchievementRepository,
) {

    @PostMapping
    fun createEvent(
        @RequestBody activityCreateRequest: ActivityCreateRequest
    ): ActivityResponse {
        return activityService.createActivity(activityCreateRequest)
    }
//
//    fun canCreateEvent(user: CustomUserDetails): Boolean {
//        return user.isOrganizer()
//    }

//    @PutMapping("/{activityId}")
//    fun updateEvent(
//        @CurrentUser user: CustomUserDetails,
//        @PathVariable activityId: Long,
//        @RequestBody eventUpdateRequest: EventUpdateRequest
//    ): EventResponse {
//        return eventService.updateEvent(user, activityId, eventUpdateRequest)
//    }

    //    @GetMapping("/{activityId}")
//    fun getEvent(@CurrentUser user: CustomUserDetails, @PathVariable activityId: Long): Activity? {
//        println(user)
//        return eventService.getEvent(activityId)
//    }
//
    @GetMapping
    fun listActivities(): List<ActivityResponse> {
        return activityService.listActivities()
    }

//    @DeleteMapping("/{activityId}")
//    fun deleteEvent(@CurrentUser user: CustomUserDetails, @PathVariable activityId: Long) {
//        eventService.deleteEvent(user, activityId)
//    }

    data class ActivityAddMembersRequest(val externalUserIds: List<String>)

    @PostMapping("/{activityId}/members")
    fun addMembers(@PathVariable activityId: Long, @RequestBody request: ActivityAddMembersRequest): ActivityResponse {
        return activityService.addMembers(activityId, request)
    }

//    @GetMapping("/joined")
//    fun listJoinedEvents(@CurrentUser user: CustomUserDetails): List<Activity> {
//        return activityService.listJoinedEvents(user)
//    }

    //    listMembers
    @GetMapping("/{activityId}/members")
    fun listMembers(@PathVariable activityId: Long): List<MemberResponse> {
        return activityService.listMembers(activityId)
    }

    //    trigger the defined effect
    data class ActivityTriggerRequest(val key: String, val params: Map<String, String>, val userId: String)

    @PostMapping("/{activityId}/trigger")
    fun trigger(@RequestBody request: ActivityTriggerRequest, @PathVariable activityId: Long) {
        return activityService.trigger(activityId, request)
    }

    // leaderboard
    @GetMapping("/{activityId}/leaderboard")
    fun leaderboard(@PathVariable activityId: Long, @RequestParam type: String): LeaderboardResponse {
        if (type == "total-points") {
            return leaderboardService.getTotalPointsLeaderboard(activityId)
        }
        return LeaderboardResponse(leaderboard = "total-points", entries = emptyList())
    }

    data class TriggerDefinition(
        val key: String,
//        val type: String,
        val params: Map<String, String>?
    )

    data class ActionDefinition(
        val key: String,
        val params: Map<String, String>
    )

    data class RuleCondition(
        val type: String,
        val params: Map<String, String>,
//        val multiple: Boolean
    )

    data class RuleCreateRequest(
        val name: String,
        val description: String?,
        val trigger: TriggerDefinition,
        val action: ActionDefinition,
        val condition: RuleCondition?
    )

    @PostMapping("/{activityId}/rules")
    fun createRule(
        @PathVariable activityId: Long,
        @RequestBody request: RuleCreateRequest
    ): RuleResponse {
        val trigger = triggerRepository.findByKey(request.trigger.key) ?: throw Exception("Trigger not found")
        val action = actionRepository.findByKey(request.action.key) ?: throw Exception("Action not found")
        val triggerType =
            request.condition?.type?.let { triggerTypeRepository.findByKey(it) }

        val activity = activityService.getActivity(activityId) ?: throw Exception("Activity not found")


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

        ruleRepository.save(rule)

        return RuleResponse.fromEntity(rule)
    }

    // create trigger per activity
    data class TriggerCreateRequest(
        val title: String,
        val description: String?,
        val key: String,
    )

    @PostMapping("/{activityId}/triggers")
    fun createTrigger(
        @PathVariable activityId: Long,
        @RequestBody request: TriggerCreateRequest
    ): RuleResponse.TriggerResponse {
        val activity = activityService.getActivity(activityId) ?: throw Exception("Activity not found")

        val trigger = Trigger(
            title = request.title,
            description = request.description,
            key = request.key,
            activity = activity
        )

        triggerRepository.save(trigger)

        return RuleResponse.TriggerResponse.fromEntity(trigger)
    }

    data class AchievementCreateRequest(
        val title: String,
        val description: String?,
        val imageUrl: String?,
    )

    data class AchievementResponse(
        val achievementId: Long,
        val title: String,
        val description: String?,
        val imageUrl: String?,
    ) {
        companion object {
            fun fromEntity(achievement: Achievement): AchievementResponse {
                return AchievementResponse(
                    achievementId = achievement.achievementId!!,
                    title = achievement.title,
                    description = achievement.description,
                    imageUrl = achievement.imageUrl,
                )
            }
        }
    }

    @PostMapping("/{activityId}/achievements")
    fun createAchievement(
        @PathVariable activityId: Long,
        @RequestBody request: AchievementCreateRequest
    ): AchievementResponse {
        val activity = activityService.getActivity(activityId) ?: throw Exception("Activity not found")

        val achievement = Achievement(
            title = request.title,
            description = request.description,
            imageUrl = request.imageUrl,
            activity = activity
        )

        achievementRepository.save(achievement)

        return AchievementResponse.fromEntity(achievement)
    }


    @GetMapping("/{activityId}/profile")
    fun getUserActivityProfile(
        @PathVariable activityId: Long,
        @RequestParam userId: String
    ): UserActivityProfileResponse {
        val existingActivity = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")

        val existingMember = existingActivity.members.find { it.user?.externalUserId == userId }
            ?: throw EntityNotFoundException("User with ID $userId not found")

        return UserActivityProfileResponse.fromEntity(existingMember)
    }
}

data class UserActivityProfileResponse(
    val user: UserResponse?,
    val points: Int,
    val achievements: List<ActivityController.AchievementResponse>
) {
    companion object {
        fun fromEntity(userActivity: UserActivity): UserActivityProfileResponse {
            return UserActivityProfileResponse(
                user = userActivity.user?.let { UserResponse.fromEntity(it) },
                points = userActivity.points ?: 0,
                achievements = userActivity.userActivityAchievements.mapNotNull {
                    it.achievement?.let { achievement ->
                        ActivityController.AchievementResponse.fromEntity(achievement)
                    }
                }
            )
        }
    }
}

class JsonSchemaValidator(
    private val objectMapper: ObjectMapper = ObjectMapper(),
    private val schemaValidatorsConfig: SchemaValidatorsConfig = SchemaValidatorsConfig()
) {

    fun validate(jsonSchema: JsonNode, jsonNode: JsonNode): JsonSchemaValidationResult {
        val factory = JsonSchemaFactory.getInstance(SpecVersionDetector.detect(jsonSchema))

        val schema = factory.getSchema(jsonSchema, schemaValidatorsConfig)
        val errors = schema.validate(jsonNode)

        return JsonSchemaValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }

    fun validate(jsonSchema: MutableMap<String, Any>, json: Map<String, String>): JsonSchemaValidationResult {
        val jsonSchemaJsonNode = objectMapper.valueToTree<JsonNode>(jsonSchema)
        val jsonNode = objectMapper.valueToTree<JsonNode>(json)
        return validate(jsonSchemaJsonNode, jsonNode)
    }
}

data class JsonSchemaValidationResult(
    val isValid: Boolean,
    val errors: Set<ValidationMessage>
) {
    companion object {
        fun valid(): JsonSchemaValidationResult {
            return JsonSchemaValidationResult(
                isValid = true,
                errors = emptySet()
            )
        }
    }
}

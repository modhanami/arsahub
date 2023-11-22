package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.*
import com.arsahub.backend.models.Achievement
import com.arsahub.backend.models.Rule
import com.arsahub.backend.repositories.*
import com.arsahub.backend.services.ActivityService
import com.arsahub.backend.services.LeaderboardService
import com.arsahub.backend.utils.JsonSchemaValidationResult
import com.arsahub.backend.utils.JsonSchemaValidator
import com.networknt.schema.SchemaValidatorsConfig
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
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
        @Valid @RequestBody activityCreateRequest: ActivityCreateRequest
    ): ActivityResponse {
        return activityService.createActivity(activityCreateRequest)
    }

    @PutMapping("/{activityId}")
    fun updateEvent(
        @PathVariable activityId: Long,
        @Valid @RequestBody activityUpdateRequest: ActivityUpdateRequest
    ): ActivityResponse {
        return activityService.updateActivity(activityId, activityUpdateRequest)
    }

    @GetMapping
    fun listActivities(): List<ActivityResponse> {
        return activityService.listActivities()
    }

    data class ActivityAddMembersRequest(val externalUserIds: List<String>)

    @PostMapping("/{activityId}/members")
    fun addMembers(@PathVariable activityId: Long, @RequestBody request: ActivityAddMembersRequest): ActivityResponse {
        return activityService.addMembers(activityId, request)
    }

    @GetMapping("/{activityId}/members")
    fun listMembers(@PathVariable activityId: Long): List<MemberResponse> {
        return activityService.listMembers(activityId)
    }

    @PostMapping("/{activityId}/trigger")
    fun trigger(@RequestBody request: ActivityTriggerRequest, @PathVariable activityId: Long) {
        return activityService.trigger(activityId, request)
    }

    @GetMapping("/{activityId}/leaderboard")
    fun leaderboard(@PathVariable activityId: Long, @RequestParam type: String): LeaderboardResponse {
        if (type == "total-points") {
            return leaderboardService.getTotalPointsLeaderboard(activityId)
        }
        return LeaderboardResponse(leaderboard = "total-points", entries = emptyList())
    }

    data class TriggerDefinition(
        val key: String,
        val params: Map<String, String>?
    )

    data class ActionDefinition(
        val key: String,
        val params: Map<String, String>
    )

    data class RuleCondition(
        val type: String,
        val params: Map<String, String>,
    )

    data class RuleCreateRequest(
        @field:Size(min = 4, max = 200, message = "Name must be between 4 and 200 characters")
        @field:NotBlank(message = "Name is required")
        val name: String?, // TODO: remove nullability and actually customize jackson-module-kotlin with the Jackson2ObjectMapperBuilderCustomizer
        @field:Size(max = 500, message = "Description cannot be longer than 500 characters")
        val description: String?,
        val trigger: TriggerDefinition,
        val action: ActionDefinition,
        val condition: RuleCondition?
    )

    @PostMapping("/{activityId}/rules")
    fun createRule(
        @PathVariable activityId: Long, @Valid @RequestBody request: RuleCreateRequest
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

    @GetMapping("/{activityId}/rules")
    fun getRules(@PathVariable activityId: Long): List<RuleResponse> {
        val activity = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")

        return activity.rules.map { RuleResponse.fromEntity(it) }
    }

    @PostMapping("/{activityId}/achievements")
    fun createAchievement(
        @PathVariable activityId: Long,
        @Valid @RequestBody request: AchievementCreateRequest
    ): AchievementResponse {
        val activity = activityService.getActivity(activityId) ?: throw Exception("Activity not found")

        val achievement = Achievement(
            title = request.title!!,
            description = request.description,
            activity = activity
        )

        achievementRepository.save(achievement)

        return AchievementResponse.fromEntity(achievement)
    }

    @PutMapping("/{activityId}/achievements/{achievementId}")
    fun updateAchievement(
        @PathVariable activityId: Long,
        @PathVariable achievementId: Long,
        @Valid @RequestBody request: AchievementUpdateRequest
    ): AchievementResponse {
        val activity = activityService.getActivity(activityId) ?: throw Exception("Activity not found")

        val achievement =
            achievementRepository.findById(achievementId).orElseThrow { Exception("Achievement not found") }

        achievement.title = request.title!!
        achievement.description = request.description
        achievement.activity = activity

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

    @GetMapping("/actions")
    fun getActions(): List<ActionResponse> {
        return actionRepository.findAll().map { ActionResponse.fromEntity(it) }
    }

}

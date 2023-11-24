package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.*
import com.arsahub.backend.models.Achievement
import com.arsahub.backend.repositories.*
import com.arsahub.backend.services.ActivityService
import com.arsahub.backend.services.LeaderboardService
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
        return activityService.createActivity(activityCreateRequest).let { ActivityResponse.fromEntity(it) }
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
        return activityService.listMembers(activityId).map { MemberResponse.fromEntity(it) }
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
        val params: Map<String, String>? = null
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
        val title: String?, // TODO: remove nullability and actually customize jackson-module-kotlin with the Jackson2ObjectMapperBuilderCustomizer
        @field:Size(max = 500, message = "Description cannot be longer than 500 characters")
        val description: String?,
        val trigger: TriggerDefinition,
        val action: ActionDefinition,
        val condition: RuleCondition? = null
    )

    @PostMapping("/{activityId}/rules")
    fun createRule(
        @PathVariable activityId: Long, @Valid @RequestBody request: RuleCreateRequest
    ): RuleResponse {
        return activityService.createRule(activityId, request).let { RuleResponse.fromEntity(it) }
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

package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.*
import com.arsahub.backend.dtos.ApiValidationError
import com.arsahub.backend.models.App
import com.arsahub.backend.repositories.AchievementRepository
import com.arsahub.backend.repositories.ActionRepository
import com.arsahub.backend.repositories.ActivityRepository
import com.arsahub.backend.security.auth.CurrentApp
import com.arsahub.backend.services.ActivityService
import com.arsahub.backend.services.LeaderboardService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/activities")
@Tag(name = "Activity API", description = "API for any activity-related operations, for all user types")
class ActivityController(
    private val activityService: ActivityService,
    private val activityRepository: ActivityRepository,
    private val leaderboardService: LeaderboardService,
    private val actionRepository: ActionRepository,
    private val achievementRepository: AchievementRepository,
) {
    @Operation(
        summary = "Create an activity an app",
        responses = [
            ApiResponse(
                responseCode = "201",
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ApiValidationError::class))]
            )
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createActivity(
        @Valid @RequestBody activityCreateRequest: ActivityCreateRequest,
        @CurrentApp app: App
    ): ActivityResponse {
        return activityService.createActivity(app, activityCreateRequest).let { ActivityResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Update an activity",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ApiValidationError::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Activity not found",
                content = [Content()]
            )
        ]
    )
    @PutMapping("/{activityId}")
    fun updateActivity(
        @PathVariable activityId: Long,
        @Valid @RequestBody activityUpdateRequest: ActivityUpdateRequest,
        @CurrentApp app: App
    ): ActivityResponse {
        if (!canPerformAction(activityId, app)) {
            throw AccessDeniedException("You do not have access to this activity")
        }
        return activityService.updateActivity(activityId, activityUpdateRequest)
    }

    fun canPerformAction(
        targetActivityId: Long,
        @CurrentApp app: App
    ): Boolean {
        val activity = activityRepository.findByIdOrNull(targetActivityId)
            ?: throw EntityNotFoundException("Activity with ID $targetActivityId not found")
        return activity.app?.id == app.id
    }

    @Operation(
        summary = "List activities for an app",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
        ]
    )
    @GetMapping
    fun listActivities(
        @CurrentApp app: App
    ): List<ActivityResponse> {
        return app.id?.let { activityService.listActivities(it).map { ActivityResponse.fromEntity(it) } } ?: emptyList()
    }

    data class ActivityAddMembersRequest(val userIds: List<String>)

    @Operation(
        summary = "Add new members to an activity",
        responses = [
            ApiResponse(
                responseCode = "201",
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ApiValidationError::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Activity not found", content = [Content()]
            )
        ]
    )
    @PostMapping("/{activityId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    fun addMembers(
        @PathVariable activityId: Long, @RequestBody request: ActivityAddMembersRequest,
        @CurrentApp app: App
    ): ActivityResponse {
        if (!canPerformAction(activityId, app)) {
            throw AccessDeniedException("You do not have access to this activity")
        }
        return activityService.addMembers(activityId, request)
    }

    @Operation(
        summary = "List members of an activity",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Activity not found", content = [Content()]
            )
        ]
    )
    @GetMapping("/{activityId}/members")
    fun listMembers(
        @PathVariable activityId: Long,
        @CurrentApp app: App
    ): List<MemberResponse> {
        if (!canPerformAction(activityId, app)) {
            throw AccessDeniedException("You do not have access to this activity")
        }
        return activityService.listMembers(activityId).map { MemberResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Send trigger for an activity member",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ApiValidationError::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Activity not found", content = [Content()]
            )
        ]
    )
    @PostMapping("/{activityId}/trigger")
    fun trigger(
        @RequestBody request: ActivityTriggerRequest, @PathVariable activityId: Long,
        @CurrentApp app: App
    ) {
        if (!canPerformAction(activityId, app)) {
            throw AccessDeniedException("You do not have access to this activity")
        }
        return activityService.trigger(activityId, request)
    }

    @Operation(
        summary = "Get activity leaderboard",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Activity not found", content = [Content()]
            )
        ]
    )
    @GetMapping("/{activityId}/leaderboard")
    fun leaderboard(@PathVariable activityId: Long, @RequestParam type: String): LeaderboardResponse {
        if (type == "total-points") {
            return leaderboardService.getTotalPointsLeaderboard(activityId)
        }
        return LeaderboardResponse(leaderboard = "total-points", entries = emptyList())
    }

    @Operation(
        summary = "Create a rule for an activity",
        responses = [
            ApiResponse(
                responseCode = "201",
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ApiValidationError::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Activity not found", content = [Content()]
            )
        ]
    )
    @PostMapping("/{activityId}/rules")
    fun createRule(
        @PathVariable activityId: Long, @Valid @RequestBody request: RuleCreateRequest,
        @CurrentApp app: App
    ): RuleResponse {
        if (!canPerformAction(activityId, app)) {
            throw AccessDeniedException("You do not have access to this activity")
        }
        return activityService.createRule(activityId, request).let { RuleResponse.fromEntity(it) }
    }

    @Operation(
        summary = "List rules of an activity",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Activity not found", content = [Content()]
            )
        ]
    )
    @GetMapping("/{activityId}/rules")
    fun getRules(
        @PathVariable activityId: Long,
        @CurrentApp app: App
    ): List<RuleResponse> {
        if (!canPerformAction(activityId, app)) {
            throw AccessDeniedException("You do not have access to this activity")
        }

        val activity = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")

        return activity.rules.map { RuleResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Create an achievement for an activity",
        responses = [
            ApiResponse(
                responseCode = "201",
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ApiValidationError::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Activity not found", content = [Content()]
            )
        ]
    )
    @PostMapping("/{activityId}/achievements")
    @ResponseStatus(HttpStatus.CREATED)
    fun createAchievement(
        @PathVariable activityId: Long,
        @Valid @RequestBody request: AchievementCreateRequest,
        @CurrentApp app: App
    ): AchievementResponse {
        if (!canPerformAction(activityId, app)) {
            throw AccessDeniedException("You do not have access to this activity")
        }

        return activityService.createAchievement(activityId, request).let { AchievementResponse.fromEntity(it) }
    }

    @Operation(
        summary = "List achievements of an activity",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Activity not found", content = [Content()]
            )
        ]
    )
    @GetMapping("/{activityId}/achievements")
    fun getAchievements(
        @PathVariable activityId: Long,
        @CurrentApp app: App
    ): List<AchievementResponse> {
        if (!canPerformAction(activityId, app)) {
            throw AccessDeniedException("You do not have access to this activity")
        }

        return activityService.listAchievements(activityId).map { AchievementResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Update an achievement for an activity",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ApiValidationError::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Activity not found", content = [Content()]
            )
        ]
    )
    @PutMapping("/{activityId}/achievements/{achievementId}")
    fun updateAchievement(
        @PathVariable activityId: Long,
        @PathVariable achievementId: Long,
        @Valid @RequestBody request: AchievementUpdateRequest,
        @CurrentApp app: App
    ): AchievementResponse {
        if (!canPerformAction(activityId, app)) {
            throw AccessDeniedException("You do not have access to this activity")
        }

        val activity = activityService.getActivity(activityId) ?: throw Exception("Activity not found")
        val achievement =
            achievementRepository.findById(achievementId).orElseThrow { Exception("Achievement not found") }

        achievement.title = request.title!!
        achievement.description = request.description
        achievement.activity = activity

        achievementRepository.save(achievement)

        return AchievementResponse.fromEntity(achievement)
    }

    @Operation(
        summary = "Get user profile for an activity",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Activity not found", content = [Content()]
            )
        ]
    )
    @GetMapping("/{activityId}/profile")
    fun getUserActivityProfile(
        @PathVariable activityId: Long,
        @RequestParam userId: String
    ): UserActivityProfileResponse {
        val existingActivity = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")
        val existingMember = existingActivity.members.find { it.appUser?.userId == userId }
            ?: throw EntityNotFoundException("User with ID $userId not found")

        return UserActivityProfileResponse.fromEntity(existingMember)
    }

    @Operation(
        summary = "List all actions (prebuilt)",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
        ]
    )
    @GetMapping("/actions")
    fun getActions(): List<ActionResponse> {
        return actionRepository.findAll().map { ActionResponse.fromEntity(it) }
    }

}

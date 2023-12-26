package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.request.*
import com.arsahub.backend.dtos.response.*
import com.arsahub.backend.models.App
import com.arsahub.backend.repositories.ActionRepository
import com.arsahub.backend.repositories.AppUserRepository
import com.arsahub.backend.repositories.RuleRepository
import com.arsahub.backend.security.auth.CurrentApp
import com.arsahub.backend.services.AppService
import com.arsahub.backend.services.LeaderboardService
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.cfg.CoercionAction
import com.fasterxml.jackson.databind.cfg.CoercionInputShape
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.type.LogicalType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/apps")
@Tag(name = "App API", description = "API for app developers")
class AppController(
    private val appService: AppService,
    private val leaderboardService: LeaderboardService,
    private val actionRepository: ActionRepository,
    private val objectMapper: ObjectMapper,
    private val ruleRepository: RuleRepository,
    private val appUserRepository: AppUserRepository
) {

    @Operation(
        summary = "Create a trigger for an app",
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
    @PostMapping("/triggers")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTrigger(
        @Valid @RequestBody request: TriggerCreateRequest,
        @CurrentApp app: App
    ): TriggerResponse {
        return appService.createTrigger(app, request).let { TriggerResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Get all triggers",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(schema = Schema(implementation = TriggerResponse::class))]
            )
        ]
    )
    @GetMapping("/triggers")
    fun getTriggers(
        @CurrentApp app: App
    ): List<TriggerResponse> {
        return appService.getTriggers(app).map { TriggerResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Send trigger for a user",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ApiValidationError::class))]
            )
        ]
    )
    @PostMapping("/trigger")
    fun trigger(
        @RequestBody json: ObjectNode,
        @CurrentApp app: App
    ) {
        val request = objectMapper.treeToValue(json, TriggerSendRequest::class.java)
        val jsonMap: Map<String, Any> = objectMapper.convertValue(json, object : TypeReference<Map<String, Any>>() {})

        return appService.trigger(app, request, jsonMap)

    }

    @Operation(
        summary = "Validate key",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ApiValidationError::class))]
            )
        ]
    )
    @PostMapping("/validate-key")
    @ResponseStatus(HttpStatus.CREATED)
    fun validateToken(
        @CurrentApp app: App
    ): Boolean {
        return true
    }

    @Operation(
        summary = "Get a specific app by UUID",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(schema = Schema(implementation = AppResponse::class))]
            )
        ]
    )
    @GetMapping
    fun getAppByUserUUID(
        @RequestParam(required = false) userUUID: UUID
    ): AppResponse {
        return appService.getAppByUserUUID(userUUID).let { AppResponse.fromEntity(it) }
    }

    //    get current authenticated app
    @Operation(
        summary = "Get current authenticated app",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(schema = Schema(implementation = AppResponse::class))]
            )
        ]
    )
    @GetMapping("/current")
    fun getCurrentApp(
        @CurrentApp app: App
    ): AppResponse {
        return AppResponse.fromEntity(app)
    }

    @Operation(
        summary = "Get a specific user by UUID", // TODO: remove this after the user auth is implemented
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(schema = Schema(implementation = UserResponse::class))]
            )
        ]
    )
    @GetMapping("/users/current")
    fun getUserByUUID(
        @RequestHeader("Authorization") authHeader: String
    ): UserResponse {
        val userUUID = UUID.fromString(authHeader.split(" ")[1])
        return appService.getUserByUUID(userUUID).let { UserResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Add a new user to an app",
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
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    fun addUserIntoApp(
        @Valid @RequestBody request: AppUserCreateRequest,
        @CurrentApp app: App
    ): AppUserResponse {
        return appService.addUser(app, request).let { AppUserResponse.fromEntity(it) }
    }

    @Operation(
        summary = "List users",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(schema = Schema(implementation = AppUserResponse::class))]
            )
        ]
    )
    @GetMapping("/users")
    fun listUsers(
        @CurrentApp app: App
    ): List<AppUserResponse> {
        return appService.listUsers(app).map { AppUserResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Get leaderboard",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
        ]
    )
    @GetMapping("/leaderboard")
    fun leaderboard(@CurrentApp app: App, @RequestParam type: String): LeaderboardResponse {
        if (type == "total-points") {
            return leaderboardService.getTotalPointsLeaderboard(app)
        }
        return LeaderboardResponse(leaderboard = "total-points", entries = emptyList())
    }

    @Operation(
        summary = "Create a rule",
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
    @PostMapping("/rules")
    fun createRule(
        @CurrentApp app: App,
        @Valid @RequestBody request: RuleCreateRequest,
    ): RuleResponse {
        return appService.createRule(app, request).let { RuleResponse.fromEntity(it) }
    }

    @Operation(
        summary = "List rules",
        responses = [
            ApiResponse(
                responseCode = "200",
            )
        ]
    )
    @GetMapping("/rules")
    fun getRules(
        @CurrentApp app: App
    ): List<RuleResponse> {
        return ruleRepository.findAllByApp(app).map { RuleResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Create an achievement",
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
    @PostMapping("/achievements")
    @ResponseStatus(HttpStatus.CREATED)
    fun createAchievement(
        @Valid @RequestBody request: AchievementCreateRequest,
        @CurrentApp app: App
    ): AchievementResponse {
        return appService.createAchievement(app, request).let { AchievementResponse.fromEntity(it) }
    }

    @Operation(
        summary = "List achievements",
        responses = [
            ApiResponse(
                responseCode = "200",
            )
        ]
    )
    @GetMapping("/achievements")
    fun getAchievements(
        @CurrentApp app: App
    ): List<AchievementResponse> {
        return appService.listAchievements(app).map { AchievementResponse.fromEntity(it) }
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
    @GetMapping("/users/{userId}")
    fun getUserProfile(
        @CurrentApp app: App,
        @PathVariable userId: String
    ): UserProfileResponse {
        val appUser = appUserRepository.findByAppAndUserId(app, userId)
            ?: throw EntityNotFoundException("User not found")

        return UserProfileResponse.fromEntity(appUser)
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

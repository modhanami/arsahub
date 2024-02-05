package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.request.AchievementCreateRequest
import com.arsahub.backend.dtos.request.AchievementSetImageRequest
import com.arsahub.backend.dtos.request.AppUserCreateRequest
import com.arsahub.backend.dtos.request.RewardCreateRequest
import com.arsahub.backend.dtos.request.RewardRedeemRequest
import com.arsahub.backend.dtos.request.RewardSetImageRequest
import com.arsahub.backend.dtos.request.RuleCreateRequest
import com.arsahub.backend.dtos.request.TriggerCreateRequest
import com.arsahub.backend.dtos.request.TriggerSendRequest
import com.arsahub.backend.dtos.response.AchievementResponse
import com.arsahub.backend.dtos.response.ApiValidationError
import com.arsahub.backend.dtos.response.AppResponse
import com.arsahub.backend.dtos.response.AppUserResponse
import com.arsahub.backend.dtos.response.LeaderboardResponse
import com.arsahub.backend.dtos.response.RewardResponse
import com.arsahub.backend.dtos.response.RuleResponse
import com.arsahub.backend.dtos.response.TransactionResponse
import com.arsahub.backend.dtos.response.TriggerResponse
import com.arsahub.backend.dtos.response.UserResponse
import com.arsahub.backend.models.App
import com.arsahub.backend.security.auth.CurrentApp
import com.arsahub.backend.services.AchievementService
import com.arsahub.backend.services.AppService
import com.arsahub.backend.services.LeaderboardService
import com.arsahub.backend.services.RuleService
import com.arsahub.backend.services.ShopService
import com.arsahub.backend.services.TriggerService
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/apps")
@Tag(name = "App API", description = "API for app developers")
class AppController(
    private val appService: AppService,
    private val leaderboardService: LeaderboardService,
    private val objectMapper: ObjectMapper,
    private val triggerService: TriggerService,
    private val ruleService: RuleService,
    private val achievementService: AchievementService,
    private val shopService: ShopService,
) {
    @Operation(
        summary = "Create a trigger for an app",
        responses = [
            ApiResponse(
                responseCode = "201",
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ApiValidationError::class))],
            ),
        ],
    )
    @PostMapping("/triggers")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTrigger(
        @Valid @RequestBody request: TriggerCreateRequest,
        @CurrentApp app: App,
    ): TriggerResponse {
        return triggerService.createTrigger(app, request).let { TriggerResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Get all triggers",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(schema = Schema(implementation = TriggerResponse::class))],
            ),
        ],
    )
    @GetMapping("/triggers")
    fun getTriggers(
        @CurrentApp app: App,
    ): List<TriggerResponse> {
        return triggerService.getTriggers(app).map { TriggerResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Send trigger for a user",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ApiValidationError::class))],
            ),
        ],
    )
    @PostMapping("/trigger")
    fun trigger(
        @RequestBody json: ObjectNode,
        @CurrentApp app: App,
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
                content = [Content(schema = Schema(implementation = ApiValidationError::class))],
            ),
        ],
    )
    @PostMapping("/validate-key")
    @ResponseStatus(HttpStatus.CREATED)
    fun validateToken(
        @CurrentApp app: App,
    ): Boolean {
        return true
    }

    @Operation(
        summary = "Get a specific app by UUID",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(schema = Schema(implementation = AppResponse::class))],
            ),
        ],
    )
    @GetMapping("/me")
    fun getAppForCurrentUser(
        @AuthenticationPrincipal jwt: org.springframework.security.oauth2.jwt.Jwt,
    ): AppResponse {
        val userId = jwt.claims["id"] as? Long ?: throw IllegalArgumentException("User ID not found in JWT")
        return appService.getAppByUserId(userId).let { AppResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Get current authenticated app",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(schema = Schema(implementation = AppResponse::class))],
            ),
        ],
    )
    @GetMapping("/current")
    fun getCurrentApp(
        @CurrentApp app: App,
    ): AppResponse {
        return AppResponse.fromEntity(app)
    }

    @Operation(
        summary = "Get a specific user by UUID", // TODO: remove this after the user auth is implemented
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(schema = Schema(implementation = UserResponse::class))],
            ),
        ],
    )
    @GetMapping("/users/current")
    fun getUserByUUID(
        @AuthenticationPrincipal jwt: org.springframework.security.oauth2.jwt.Jwt,
    ): UserResponse {
        val userId = jwt.claims["id"] as? Long ?: throw IllegalArgumentException("User ID not found in JWT")
        return appService.getUserById(userId).let { UserResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Add a new user to an app",
        responses = [
            ApiResponse(
                responseCode = "201",
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ApiValidationError::class))],
            ),
        ],
    )
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    fun addUserIntoApp(
        @Valid @RequestBody request: AppUserCreateRequest,
        @CurrentApp app: App,
    ): AppUserResponse {
        return appService.addUser(app, request).let { AppUserResponse.fromEntity(it) }
    }

    @Operation(
        summary = "List users",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(schema = Schema(implementation = AppUserResponse::class))],
            ),
        ],
    )
    @GetMapping("/users")
    fun listUsers(
        @CurrentApp app: App,
    ): List<AppUserResponse> {
        return appService.listUsers(app).map { AppUserResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Get leaderboard",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
        ],
    )
    @GetMapping("/{appId}/leaderboard")
    fun leaderboard(
        @RequestParam type: String,
        @PathVariable appId: Long,
    ): LeaderboardResponse {
        if (type == "total-points") {
            return leaderboardService.getTotalPointsLeaderboard(appId)
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
                content = [Content(schema = Schema(implementation = ApiValidationError::class))],
            ),
        ],
    )
    @PostMapping("/rules")
    @ResponseStatus(HttpStatus.CREATED)
    fun createRule(
        @CurrentApp app: App,
        @Valid @RequestBody request: RuleCreateRequest,
    ): RuleResponse {
        return ruleService.createRule(app, request).let { RuleResponse.fromEntity(it) }
    }

    @Operation(
        summary = "List rules",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
        ],
    )
    @GetMapping("/rules")
    fun getRules(
        @CurrentApp app: App,
    ): List<RuleResponse> {
        return ruleService.listRules(app).map { RuleResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Create an achievement",
        responses = [
            ApiResponse(
                responseCode = "201",
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ApiValidationError::class))],
            ),
        ],
    )
    @PostMapping("/achievements")
    @ResponseStatus(HttpStatus.CREATED)
    fun createAchievement(
        @Valid @RequestBody request: AchievementCreateRequest,
        @CurrentApp app: App,
    ): AchievementResponse {
        return achievementService.createAchievement(app, request).let { AchievementResponse.fromEntity(it) }
    }

    @PostMapping("/achievements/{achievementId}/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun setImageForAchievement(
        @CurrentApp app: App,
        @PathVariable achievementId: Long,
        @RequestPart("image") image: MultipartFile,
    ): AchievementResponse {
        return achievementService.setImageForAchievement(
            app,
            AchievementSetImageRequest(
                achievementId = achievementId,
                image = image,
            ),
        ).let { AchievementResponse.fromEntity(it) }
    }

    @Operation(
        summary = "List achievements",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
        ],
    )
    @GetMapping("/achievements")
    fun getAchievements(
        @CurrentApp app: App,
    ): List<AchievementResponse> {
        return achievementService.listAchievements(app).map { AchievementResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Get user profile for an activity",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Activity not found",
                content = [Content()],
            ),
        ],
    )
    @GetMapping("/{appId}/users/{userId}")
    fun getUser(
        @PathVariable appId: Long,
        @PathVariable userId: String,
    ): AppUserResponse {
        return appService.getAppUserOrThrow(appId, userId).let { AppUserResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Get rewards",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
        ],
    )
    @GetMapping("/shop/rewards")
    fun getRewards(
        @CurrentApp app: App,
    ): List<RewardResponse> {
        return shopService.getRewards(app).map { RewardResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Redeem a reward",
        responses = [
            ApiResponse(
                responseCode = "200",
            ),
        ],
    )
    @PostMapping("/shop/rewards/redeem")
    fun redeemReward(
        @CurrentApp app: App,
        @Valid @RequestBody request: RewardRedeemRequest,
    ): TransactionResponse {
        return shopService.redeemReward(app, request).let { TransactionResponse.fromEntity(it) }
    }

    @Operation(
        summary = "Create a reward",
        responses = [
            ApiResponse(
                responseCode = "201",
            ),
        ],
    )
    @PostMapping("/shop/rewards")
    @ResponseStatus(HttpStatus.CREATED)
    fun createReward(
        @CurrentApp app: App,
        @Valid @RequestBody request: RewardCreateRequest,
    ): RewardResponse {
        return shopService.createReward(app, request).let { RewardResponse.fromEntity(it) }
    }

    @PostMapping("/shop/rewards/{rewardId}/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun setImageForReward(
        @CurrentApp app: App,
        @PathVariable rewardId: Long,
        @RequestPart("image") image: MultipartFile,
    ): RewardResponse {
        return shopService.setImageForReward(
            app,
            RewardSetImageRequest(
                rewardId = rewardId,
                image = image,
            ),
        ).let { RewardResponse.fromEntity(it) }
    }
}

package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.*
import com.arsahub.backend.models.App
import com.arsahub.backend.repositories.*
import com.arsahub.backend.security.auth.CurrentApp
import com.arsahub.backend.services.ActivityService
import com.arsahub.backend.services.AppService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/apps")
@Tag(name = "App API", description = "API for app developers")
class AppController(
    private val customUnitRepository: CustomUnitRepository,
    private val triggerRepository: TriggerRepository,
    private val activityRepository: ActivityRepository,
    private val userActivityProgressRepository: UserActivityProgressRepository,
    private val ruleProgressTimeRepository: RuleProgressTimeRepository,
    private val activityService: ActivityService,
    private val appService: AppService,
) {
//    @Operation(
//        summary = "Create a custom unit (globally)", // TODO: make custom unit scoped to app
//        description = "Create a custom unit that can be used in activities and rules. Following triggers will be created automatically: {custom_unit_key}_reached",
//        responses = [
//            ApiResponse(
//                responseCode = "201",
//            ),
//            ApiResponse(
//                responseCode = "400",
//                content = [Content(schema = Schema(implementation = ApiValidationError::class))]
//            ),
//            ApiResponse(
//                responseCode = "409",
//                description = "Custom unit with the same key already exists",
//                content = [Content(schema = Schema(implementation = ApiError::class))]
//            )
//        ]
//    )
//    @PostMapping("/custom-units")
//    fun createCustomUnit(
//        @RequestBody request: CustomUnitCreateRequest
//    ): CustomUnitResponse {
//        val existingCustomUnit = customUnitRepository.findByKey(request.key)
//        if (existingCustomUnit != null) {
//            throw ConflictException("Custom unit with key ${request.key} already exists")
//        }
//        val customUnit = CustomUnit(
//            name = request.name,
//            key = request.key,
//        )
//
//        customUnitRepository.save(customUnit)
//        val triggerSchema = """
//        {
//            "type": "object",
//            "${'$'}schema": "http://json-schema.org/draft-04/schema#",
//            "required": [
//                "value"
//            ],
//            "properties": {
//                "value": {
//                    "type": "number"
//                }
//            }
//        }
//    """.trimIndent()
//        val schemaValidatorsConfig = SchemaValidatorsConfig()
//        schemaValidatorsConfig.isTypeLoose = true
//        val validator = JsonSchemaValidator(schemaValidatorsConfig = schemaValidatorsConfig)
//        val trigger = Trigger(
//            title = "${customUnit.name} reached",
//            description = "Triggered when ${customUnit.name} reached a certain value",
//            key = "${customUnit.key}_reached",
//            jsonSchema = validator.convertJsonStringToMap(triggerSchema).toMutableMap(),
//        )
//
//        triggerRepository.save(trigger)
//
//        return CustomUnitResponse.fromEntity(customUnit)
//    }

    data class IncrementUnitRequest(
        val unitKey: String,
        val amount: Int,
        val userId: String,
    )

//    @Operation(
//        summary = "Increment a custom unit for a user in an activity",
//        responses = [
//            ApiResponse(
//                responseCode = "200",
//            ),
//            ApiResponse(
//                responseCode = "400",
//                content = [Content(schema = Schema(implementation = ApiValidationError::class))]
//            ),
//            ApiResponse(
//                responseCode = "404",
//                content = [Content(schema = Schema(implementation = ApiError::class))]
//            )
//        ]
//    )
//    @PostMapping("/{activityId}/increment-unit")
//    fun incrementUnit(
//        @PathVariable activityId: Long,
//        @RequestBody request: IncrementUnitRequest
//    ) {
//        val customUnit = customUnitRepository.findByKey(request.unitKey)
//            ?: throw EntityNotFoundException("Custom unit with key ${request.unitKey} not found")
//        val activity = activityRepository.findByIdOrNull(activityId)
//            ?: throw EntityNotFoundException("Activity with ID $activityId not found")
//        val appUserActivity = activity.members.find { it.appUser?.userId == request.userId }
//            ?: throw EntityNotFoundException("User with ID ${request.userId} not found")
//        var currentProgress = appUserActivity.userActivityProgresses.find { it.customUnit?.key == request.unitKey }
//        if (currentProgress != null) {
//            currentProgress.progressValue = currentProgress.progressValue?.plus(request.amount)
//            userActivityProgressRepository.save(currentProgress)
//
//            println("Incremented progress ${customUnit.name} for user ${appUserActivity.appUser?.userId} in activity ${activity.title} by ${request.amount} to ${currentProgress.progressValue}")
//        } else {
//            currentProgress = UserActivityProgress(
//                activity = activity,
//                appUserActivity = appUserActivity,
//                customUnit = customUnit,
//                progressValue = request.amount
//            )
//            userActivityProgressRepository.save(currentProgress)
//
//            println("Created progress ${customUnit.name} for user ${appUserActivity.appUser?.userId} in activity ${activity.title} with value ${request.amount}")
//        }
//        val matchingRules = activity.rules.filter { it.trigger?.key == "${customUnit.key}_reached" }
//        println("Found ${matchingRules.size} rules for ${customUnit.name} reached")
//        matchingRules.forEach { rule ->
//            val value = rule.triggerParams?.get("value")?.toString()?.toInt()
//                ?: throw Exception("Value not found for rule ${rule.title} (${rule.id})")
//
//            if ((currentProgress.progressValue ?: 0) < value) {
//                println("Skipping rule ${rule.title} (${rule.id}) for user ${appUserActivity.appUser?.userId} in activity ${activity.title} because progress is ${currentProgress.progressValue} and value is $value")
//                return@forEach
//            }
//            // check if the rule has already been activated from rule_progress_time
//            if (ruleProgressTimeRepository.findByRuleAndAppUserActivity(rule, appUserActivity) != null) {
//                println("Skipping rule ${rule.title} (${rule.id}) for user ${appUserActivity.appUser?.userId} in activity ${activity.title} because it has already been activated")
//                return@forEach
//            }
//
//            println("User reached ${currentProgress.progressValue} ${customUnit.name}, activating rule ${rule.title} (${rule.id})")
//
//            activityService.trigger(
//                activityId,
//                ActivityTriggerRequest(
//                    key = "${customUnit.key}_reached",
//                    params = emptyMap(),
//                    userId = request.userId
//                )
//            )
//            // mark the rule as activated for the user
//            val ruleProgress = RuleProgressTime(
//                rule = rule, appUserActivity = appUserActivity, progress = 1, completedAt = Instant.now()
//            )
//
//            ruleProgressTimeRepository.save(ruleProgress)
//        }
//    }

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
        return appService.createTrigger(app, request)
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
        return app.id?.let { appService.getTriggers(it).map { TriggerResponse.fromEntity(it) } } ?: emptyList()
    }

//    @Operation(
//        summary = "Create an app",
//        responses = [
//            ApiResponse(
//                responseCode = "201",
//            ),
//            ApiResponse(
//                responseCode = "400",
//                content = [Content(schema = Schema(implementation = ApiValidationError::class))]
//            )
//        ]
//    )
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    fun createApp(@Valid @RequestBody request: AppCreateRequest): AppCreateResponse {
//        return appService.createApp(request).let { AppCreateResponse.fromEntity(it.app, it.apiKey) }
//    }

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
        @PathVariable appId: Long,
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

//    data class RuleTemplateCreateRequest(
//        // same as RuleCreateRequest, without the unused `condition` field
//        @field:Size(min = 4, max = 200, message = "Name must be between 4 and 200 characters")
//        @field:NotBlank(message = "Name is required")
//        val name: String?, // TODO: remove nullability and actually customize jackson-module-kotlin with the Jackson2ObjectMapperBuilderCustomizer
//        @field:Size(max = 500, message = "Description cannot be longer than 500 characters")
//        val description: String?,
//        val trigger: TriggerDefinition,
//        val action: ActionDefinition,
//    )
//
//    @Operation(
//        summary = "Create a rule template",
//        responses = [
//            ApiResponse(
//                responseCode = "201",
//            ),
//            ApiResponse(
//                responseCode = "400",
//                content = [Content(schema = Schema(implementation = ApiValidationError::class))]
//            ),
//            ApiResponse(
//                responseCode = "404",
//                description = "Activity not found", content = [Content()]
//            )
//        ]
//    )
//    @PostMapping("/rule-templates")
//    @ResponseStatus(HttpStatus.CREATED)
//    fun createRuleTemplate(
//        @PathVariable activityId: Long, @Valid @RequestBody request: RuleTemplateCreateRequest
//    ): RuleTemplateResponse {
//        return appService.createRuleTemplate(request).let { RuleTemplateResponse.fromEntity(it) }
//    }

    @Operation(
        summary = "List app templates",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(schema = Schema(implementation = AppTemplateResponse::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "App with this name already exists",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            )
        ]
    )
    @GetMapping("/templates")
    fun listAppTemplates(
    ): List<AppTemplateResponse> {
        return appService.listAppTemplates().map { AppTemplateResponse.fromEntity(it) }
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
}

package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.*
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.models.CustomUnit
import com.arsahub.backend.models.RuleProgressTime
import com.arsahub.backend.models.Trigger
import com.arsahub.backend.models.UserActivityProgress
import com.arsahub.backend.repositories.*
import com.arsahub.backend.services.ActivityService
import com.arsahub.backend.services.IntegrationService
import com.arsahub.backend.utils.JsonSchemaValidator
import com.networknt.schema.SchemaValidatorsConfig
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api/integrations")
@Tag(name = "Integration API", description = "API for integration developers")
class IntegrationController(
    private val customUnitRepository: CustomUnitRepository,
    private val triggerRepository: TriggerRepository,
    private val activityRepository: ActivityRepository,
    private val userActivityProgressRepository: UserActivityProgressRepository,
    private val ruleProgressTimeRepository: RuleProgressTimeRepository,
    private val activityService: ActivityService,
    private val integrationService: IntegrationService,
) {
    @Operation(
        summary = "Create a custom unit (globally)", // TODO: make custom unit scoped to integration
        description = "Create a custom unit that can be used in activities and rules. Following triggers will be created automatically: {custom_unit_key}_reached",
        responses = [
            ApiResponse(
                responseCode = "201",
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ApiValidationError::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Custom unit with the same key already exists",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            )
        ]
    )
    @PostMapping("/custom-units")
    fun createCustomUnit(
        @RequestBody request: CustomUnitCreateRequest
    ): CustomUnitResponse {
        val existingCustomUnit = customUnitRepository.findByKey(request.key)
        if (existingCustomUnit != null) {
            throw ConflictException("Custom unit with key ${request.key} already exists")
        }
        val customUnit = CustomUnit(
            name = request.name,
            key = request.key,
        )

        customUnitRepository.save(customUnit)
        val triggerSchema = """
        {
            "type": "object",
            "${'$'}schema": "http://json-schema.org/draft-04/schema#",
            "required": [
                "value"
            ],
            "properties": {
                "value": {
                    "type": "number"
                }
            }
        }
    """.trimIndent()
        val schemaValidatorsConfig = SchemaValidatorsConfig()
        schemaValidatorsConfig.isTypeLoose = true
        val validator = JsonSchemaValidator(schemaValidatorsConfig = schemaValidatorsConfig)
        val trigger = Trigger(
            title = "${customUnit.name} reached",
            description = "Triggered when ${customUnit.name} reached a certain value",
            key = "${customUnit.key}_reached",
            jsonSchema = validator.convertJsonStringToMap(triggerSchema).toMutableMap(),
        )

        triggerRepository.save(trigger)

        return CustomUnitResponse.fromEntity(customUnit)
    }

    data class IncrementUnitRequest(
        val unitKey: String,
        val amount: Int,
        val userId: String,
    )

    @Operation(
        summary = "Increment a custom unit for a user in an activity",
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
                content = [Content(schema = Schema(implementation = ApiError::class))]
            )
        ]
    )
    @PostMapping("/{activityId}/increment-unit")
    fun incrementUnit(
        @PathVariable activityId: Long,
        @RequestBody request: IncrementUnitRequest
    ) {
        val customUnit = customUnitRepository.findByKey(request.unitKey)
            ?: throw EntityNotFoundException("Custom unit with key ${request.unitKey} not found")
        val activity = activityRepository.findByIdOrNull(activityId)
            ?: throw EntityNotFoundException("Activity with ID $activityId not found")
        val userActivity = activity.members.find { it.user?.externalUserId == request.userId }
            ?: throw EntityNotFoundException("User with ID ${request.userId} not found")
        var currentProgress = userActivity.userActivityProgresses.find { it.customUnit?.key == request.unitKey }
        if (currentProgress != null) {
            currentProgress.progressValue = currentProgress.progressValue?.plus(request.amount)
            userActivityProgressRepository.save(currentProgress)

            println("Incremented progress ${customUnit.name} for user ${userActivity.user?.externalUserId} in activity ${activity.title} by ${request.amount} to ${currentProgress.progressValue}")
        } else {
            currentProgress = UserActivityProgress(
                activity = activity,
                userActivity = userActivity,
                customUnit = customUnit,
                progressValue = request.amount
            )
            userActivityProgressRepository.save(currentProgress)

            println("Created progress ${customUnit.name} for user ${userActivity.user?.externalUserId} in activity ${activity.title} with value ${request.amount}")
        }
        val matchingRules = activity.rules.filter { it.trigger?.key == "${customUnit.key}_reached" }
        println("Found ${matchingRules.size} rules for ${customUnit.name} reached")
        matchingRules.forEach { rule ->
            val value = rule.triggerParams?.get("value")?.toString()?.toInt()
                ?: throw Exception("Value not found for rule ${rule.title} (${rule.id})")

            if ((currentProgress.progressValue ?: 0) < value) {
                println("Skipping rule ${rule.title} (${rule.id}) for user ${userActivity.user?.externalUserId} in activity ${activity.title} because progress is ${currentProgress.progressValue} and value is $value")
                return@forEach
            }
            // check if the rule has already been activated from rule_progress_time
            if (ruleProgressTimeRepository.findByRuleAndUserActivity(rule, userActivity) != null) {
                println("Skipping rule ${rule.title} (${rule.id}) for user ${userActivity.user?.externalUserId} in activity ${activity.title} because it has already been activated")
                return@forEach
            }

            println("User reached ${currentProgress.progressValue} ${customUnit.name}, activating rule ${rule.title} (${rule.id})")

            activityService.trigger(
                activityId,
                ActivityTriggerRequest(
                    key = "${customUnit.key}_reached",
                    params = emptyMap(),
                    userId = request.userId
                )
            )
            // mark the rule as activated for the user
            val ruleProgress = RuleProgressTime(
                rule = rule, userActivity = userActivity, progress = 1, completedAt = Instant.now()
            )

            ruleProgressTimeRepository.save(ruleProgress)
        }
    }

    @Operation(
        summary = "Create a trigger (globally)", // TODO: make trigger scoped to integration
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
    fun createTrigger(@Valid @RequestBody request: TriggerCreateRequest): TriggerResponse {
        return integrationService.createTrigger(request)
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
    fun getTriggers(): List<TriggerResponse> {
        return integrationService.getTriggers().map { TriggerResponse.fromEntity(it) }
    }


    @Operation(
        summary = "Create an integration",
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
    fun createIntegration(@Valid @RequestBody request: IntegrationCreateRequest): IntegrationResponse {
        return integrationService.createIntegration(request).let { IntegrationResponse.fromEntity(it) }
    }

    @Operation(
        summary = "List integrations",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(schema = Schema(implementation = IntegrationResponse::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Integration with this name already exists",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            )
        ]
    )
    @GetMapping
    fun listIntegrations(
        @RequestParam userId: Long // TODO: for testing purposes only, should be removed and retrieved from the session token, etc.
    ): List<IntegrationResponse> {
        return integrationService.listIntegrations(userId).map { IntegrationResponse.fromEntity(it) }
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
//        return integrationService.createRuleTemplate(request).let { RuleTemplateResponse.fromEntity(it) }
//    }
}

package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.*
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.models.CustomUnit
import com.arsahub.backend.models.RuleProgressTime
import com.arsahub.backend.models.Trigger
import com.arsahub.backend.models.UserActivityProgress
import com.arsahub.backend.repositories.*
import com.arsahub.backend.services.ActivityService
import com.arsahub.backend.utils.JsonSchemaValidator
import com.networknt.schema.SchemaValidatorsConfig
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api/integrations")
class IntegrationController(
    private val customUnitRepository: CustomUnitRepository,
    private val triggerRepository: TriggerRepository,
    private val activityRepository: ActivityRepository,
    private val userActivityProgressRepository: UserActivityProgressRepository,
    private val ruleProgressTimeRepository: RuleProgressTimeRepository,
    private val activityService: ActivityService,
) {
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

    @PostMapping("/triggers")
    fun createTrigger(@Valid @RequestBody request: TriggerCreateRequest): TriggerResponse {
        val trigger = Trigger(
            title = request.title,
            description = request.description,
            key = request.key
        )
        return TriggerResponse.fromEntity(triggerRepository.save(trigger))
    }

    @GetMapping("/triggers")
    fun getTriggers(): List<TriggerResponse> {
        return triggerRepository.findAll().map { TriggerResponse.fromEntity(it) }
    }

}

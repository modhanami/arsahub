package com.arsahub.backend.services

import com.arsahub.backend.dtos.request.TriggerCreateRequest
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.exceptions.NotFoundException
import com.arsahub.backend.models.App
import com.arsahub.backend.models.Trigger
import com.arsahub.backend.models.TriggerField
import com.arsahub.backend.models.TriggerFieldType
import com.arsahub.backend.repositories.TriggerRepository
import com.arsahub.backend.utils.KeyUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

class TriggerConflictException :
    ConflictException("Trigger with the same title already exists")

class TriggerNotFoundException(triggerKey: String) : NotFoundException("Trigger with key $triggerKey not found")

@Service
class TriggerService(
    private val triggerRepository: TriggerRepository,
) {
    private val logger = KotlinLogging.logger {}

    fun getTriggers(
        app: App,
        withBuiltIn: Boolean = false,
    ): List<Trigger> {
        return triggerRepository.findAllByAppId(app.id!!) +
            if (withBuiltIn) triggerRepository.findAllByAppIdIsNull() else emptyList()
    }

    fun getTriggerOrThrow(
        key: String,
        app: App,
    ): Trigger {
        return triggerRepository.findByKeyAndApp(key, app) ?: throw TriggerNotFoundException(key)
    }

    fun getBuiltInTriggerOrThrow(key: String): Trigger {
        return triggerRepository.findByKey(key) ?: throw TriggerNotFoundException(key)
    }

    fun createTrigger(
        app: App,
        request: TriggerCreateRequest,
    ): Trigger {
        logger.debug {
            "Received trigger create request for app ${app.title} (${app.id}): " +
                "Name = ${request.title}"
        }

        val autoKey = KeyUtils.generateKeyFromTitle(request.title!!) ?: throw IllegalArgumentException("Invalid title")
        logger.debug { "Generated key: $autoKey" }
        if (autoKey.isBlank()) {
            logger.error { "Generated key is empty" }
            throw IllegalArgumentException("Invalid title")
        }

        // validate uniqueness of key in app
        val existingTrigger = triggerRepository.findByKeyAndApp(autoKey, app)
        if (existingTrigger != null) {
            logger.error { "Trigger with key $autoKey already exists" }
            throw TriggerConflictException()
        }

        // validate field definitions
        if (request.fields != null) {
            logger.debug { "Validating ${request.fields.size} field definitions" }
            request.fields.forEach { field ->
                field.type?.let { require(TriggerFieldType.supports(it)) { "Invalid field type: ${field.type}" } }
            }
        }

        // save trigger and fields
        val trigger =
            Trigger(
                title = request.title,
                description = request.description,
                key = autoKey,
                app = app,
            )
        val triggerFields =
            request.fields?.map { field ->
                TriggerField(
                    key = field.key,
                    type = field.type!!,
                    label = field.label,
                    trigger = trigger,
                )
            }?.toMutableSet() ?: emptySet()

        trigger.fields = triggerFields.toMutableSet()

        val savedTrigger = triggerRepository.save(trigger)

        logger.info {
            "Trigger ${savedTrigger.title} (${savedTrigger.id}) created for app " +
                "${app.title} (${app.id})"
        }

        return savedTrigger
    }

    fun validateParamsAgainstTriggerFields(
        conditions: Map<String, Any>?,
        fields: Iterable<TriggerField>,
    ) {
        for (conditionKey in conditions?.keys ?: emptyList()) {
            // ensure the key and it's value are not empty
            require(conditionKey.isNotBlank()) { "Condition key cannot be empty" }
            val conditionValue = conditions?.get(conditionKey)
            require(conditionValue != null && conditionValue.toString().isNotBlank()) {
                "Condition value cannot be empty"
            }

            val targetField = fields.find { it.key == conditionKey } ?: continue
            val targetFieldType = TriggerFieldType.fromString(targetField.type!!)

            requireNotNull(targetFieldType) {
                val message = "Field ${targetField.key} has an invalid type: ${targetField.type}"
                logger.error { message }
                message
            }

            when (targetFieldType) {
                TriggerFieldType.INTEGER ->
                    require(
                        conditionValue is Int,
                    ) { "Field ${targetField.key} is not an integer, got $conditionValue" }

                TriggerFieldType.TEXT ->
                    require(
                        conditionValue is String,
                    ) { "Field ${targetField.key} is not a text, got $conditionValue" }
            }
        }
    }
}

package com.arsahub.backend.services

import com.arsahub.backend.dtos.request.TriggerCreateRequest
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.exceptions.NotFoundException
import com.arsahub.backend.models.App
import com.arsahub.backend.models.Trigger
import com.arsahub.backend.models.TriggerField
import com.arsahub.backend.models.TriggerFieldType
import com.arsahub.backend.repositories.TriggerRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

class TriggerConflictException(triggerKey: String) :
    ConflictException("Trigger with key $triggerKey already exists. Try another title that gives a unique key.")

class TriggerNotFoundException(triggerKey: String) : NotFoundException("Trigger with key $triggerKey not found")

@Service
class TriggerService(
    private val triggerRepository: TriggerRepository,
) {
    private val logger = KotlinLogging.logger {}

    fun getTriggers(app: App): List<Trigger> {
//        return triggerRepository.findAllByAppId(app.id!!)
        return triggerRepository.findAll()
    }

    fun getTriggerOrThrow(
        key: String,
        app: App,
    ): Trigger {
        return triggerRepository.findByKeyAndApp(key, app) ?: throw TriggerNotFoundException(key)
    }

    fun createTrigger(
        app: App,
        request: TriggerCreateRequest,
    ): Trigger {
        logger.debug {
            "Received trigger create request for app ${app.title} (${app.id}): " +
                "Name = ${request.title}, Key = ${request.key}"
        }

        // validate uniqueness of key in app
        val existingTrigger = triggerRepository.findByKeyAndApp(request.key!!, app)
        if (existingTrigger != null) {
            throw TriggerConflictException(request.key)
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
                key = request.key,
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
        params: Map<String, Any>?,
        fields: Iterable<TriggerField>,
    ) {
        for (paramKey in params?.keys ?: emptyList()) {
            val field = fields.find { it.key == paramKey } ?: continue
            val fieldValue = params?.get(paramKey)

            val fieldType = TriggerFieldType.fromString(field.type!!)

            requireNotNull(fieldType) {
                val message = "Field ${field.key} has an invalid type: ${field.type}"
                logger.error { message }
                message
            }

            when (fieldType) {
                TriggerFieldType.INTEGER ->
                    require(
                        fieldValue is Int,
                    ) { "Field ${field.key} is not an integer, got $fieldValue" }

                TriggerFieldType.TEXT ->
                    require(
                        fieldValue is String,
                    ) { "Field ${field.key} is not a text, got $fieldValue" }
            }
        }
    }
}

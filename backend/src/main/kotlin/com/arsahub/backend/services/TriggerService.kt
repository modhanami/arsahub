package com.arsahub.backend.services

import com.arsahub.backend.dtos.request.TriggerCreateRequest
import com.arsahub.backend.dtos.request.TriggerUpdateRequest
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.exceptions.NotFoundException
import com.arsahub.backend.models.App
import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.Trigger
import com.arsahub.backend.models.TriggerField
import com.arsahub.backend.models.TriggerFieldType
import com.arsahub.backend.repositories.RuleRepository
import com.arsahub.backend.repositories.TriggerRepository
import com.arsahub.backend.utils.KeyUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

class TriggerConflictException :
    ConflictException("Trigger with the same title already exists")

class TriggerNotFoundException : NotFoundException("Trigger not found")

@Service
class TriggerService(
    private val triggerRepository: TriggerRepository,
    private val ruleRepository: RuleRepository,
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
        return triggerRepository.findByKeyAndApp(key, app) ?: throw TriggerNotFoundException()
    }

    fun getBuiltInTriggerOrThrow(key: String): Trigger {
        return triggerRepository.findByKey(key) ?: throw TriggerNotFoundException()
    }

    fun createTrigger(
        app: App,
        request: TriggerCreateRequest,
    ): Trigger {
        logger.debug {
            "Received trigger create request for app ${app.title} (${app.id}): " +
                "Name = ${request.title}"
        }

        val autoKey = KeyUtils.generateKeyFromTitle(request.title!!)
        logger.debug { "Generated key: $autoKey" }
        if (autoKey.isBlank()) {
            logger.error { "Generated key is empty" }
            throw IllegalArgumentException("Invalid title")
        }

        // validate uniqueness of key and title (case-sensitive) in app
        if (triggerRepository.findByKeyAndApp(autoKey, app) != null) {
            logger.error { "Trigger with key $autoKey already exists" }
            throw TriggerConflictException()
        }
        if (triggerRepository.findByTitleAndApp(request.title, app) != null) {
            logger.error { "Trigger with title ${request.title} already exists" }
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

    fun deleteTrigger(
        currentApp: App,
        triggerId: Long,
    ) {
        val trigger = triggerRepository.findByIdOrNull(triggerId) ?: throw TriggerNotFoundException()
        assertCanDeleteTrigger(currentApp, trigger)

        assertTriggerNotInUse(currentApp, trigger)

        triggerRepository.delete(trigger)
    }

    private fun assertCanDeleteTrigger(
        currentApp: App,
        trigger: Trigger,
    ) {
        if (trigger.app!!.id != currentApp.id) {
            throw TriggerNotFoundException()
        }
    }

    class TriggerInUseException :
        ConflictException("Trigger is used by one or more rules")

    private fun assertTriggerNotInUse(
        currentApp: App,
        trigger: Trigger,
    ) {
        val rules = getMatchingRules(currentApp, trigger)
        if (rules.isNotEmpty()) {
            throw TriggerInUseException()
        }
    }

    // TODO: duplicated from RuleService
    fun getMatchingRules(
        app: App,
        trigger: Trigger,
    ): List<Rule> {
        return ruleRepository.findAllByAppAndTrigger_Key(app, trigger.key!!)
    }

    fun updateTrigger(
        app: App,
        triggerId: Long,
        request: TriggerUpdateRequest,
    ): Trigger {
        val trigger = triggerRepository.findByIdOrNull(triggerId) ?: throw TriggerNotFoundException()
        assertCanUpdateTrigger(app, trigger)

        request.title?.also { trigger.title = it }
        request.description?.also { trigger.description = it }

        return triggerRepository.save(trigger)
    }

    private fun assertCanUpdateTrigger(
        app: App,
        trigger: Trigger,
    ) {
        if (trigger.app!!.id != app.id) {
            throw TriggerNotFoundException()
        }
    }
}

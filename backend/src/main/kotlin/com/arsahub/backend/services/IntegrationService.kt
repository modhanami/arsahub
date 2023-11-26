package com.arsahub.backend.services

import com.arsahub.backend.dtos.IntegrationCreateRequest
import com.arsahub.backend.dtos.TriggerCreateRequest
import com.arsahub.backend.dtos.TriggerResponse
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.models.ExternalSystem
import com.arsahub.backend.models.IntegrationTemplate
import com.arsahub.backend.models.Trigger
import com.arsahub.backend.repositories.ExternalSystemRepository
import com.arsahub.backend.repositories.IntegrationTemplateRepository
import com.arsahub.backend.repositories.TriggerRepository
import com.arsahub.backend.repositories.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class IntegrationService(
    private val triggerRepository: TriggerRepository,
    private val externalSystemRepository: ExternalSystemRepository,
    private val userRepository: UserRepository,
    private val integrationTemplateRepository: IntegrationTemplateRepository

) {
    fun createTrigger(request: TriggerCreateRequest): TriggerResponse {
        // TODO: enforce unique key per integration
        val trigger = Trigger(
            title = request.title,
            description = request.description,
            key = request.key
        )
        return TriggerResponse.fromEntity(triggerRepository.save(trigger))
    }

    fun getTriggers(): List<Trigger> {
        return triggerRepository.findAll()
    }

    fun createIntegration(request: IntegrationCreateRequest): ExternalSystem {
        val user = userRepository.findById(request.createdBy).orElseThrow { Exception("User not found") }

        // name should be unique for each user
        externalSystemRepository.findByTitleAndCreatedBy(request.name, user)?.let {
            throw ConflictException("Integration with this name already exists for this user")
        }

        val integration = ExternalSystem(
            title = request.name,
            createdBy = user,
            apiKey = UUID.randomUUID()
        )
        val savedIntegration = externalSystemRepository.save(integration)

        if (request.templateId == null) {
            return savedIntegration
        }

        println("Populating for template ${request.templateId}")
        val template =
            integrationTemplateRepository.findById(request.templateId).orElseThrow { Exception("Template not found") }
        template.triggerTemplates.forEach {
            val trigger = Trigger(
                title = it.title,
                description = it.description,
                key = it.key,
                integration = savedIntegration
            )
            triggerRepository.save(trigger)
            println("Created trigger ${it.title} for integration ${savedIntegration.title} (${savedIntegration.id})")
        }

        return savedIntegration
    }

    fun listIntegrations(userId: Long): List<ExternalSystem> {
        val user = userRepository.findById(userId).orElseThrow { Exception("User not found") }
        return externalSystemRepository.findAllByCreatedBy(user)
    }

    fun listIntegrationTemplates(): List<IntegrationTemplate> {
        return integrationTemplateRepository.findAll()
    }
}


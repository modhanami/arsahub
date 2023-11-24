package com.arsahub.backend.services

import com.arsahub.backend.dtos.TriggerCreateRequest
import com.arsahub.backend.dtos.TriggerResponse
import com.arsahub.backend.models.ExternalSystem
import com.arsahub.backend.models.Trigger
import com.arsahub.backend.repositories.ExternalSystemRepository
import com.arsahub.backend.repositories.TriggerRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class IntegrationService(
    private val triggerRepository: TriggerRepository,
    private val externalSystemRepository: ExternalSystemRepository
) {
    fun createTrigger(request: TriggerCreateRequest): TriggerResponse {
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

    data class IntegrationCreateRequest(
        val title: String,
        val description: String,
    )

    fun createIntegration(request: IntegrationCreateRequest): ExternalSystem {
        val integration = ExternalSystem(
            title = request.title,
            description = request.description,
            apiKey = UUID.randomUUID()
        )
        return externalSystemRepository.save(integration)
    }
}


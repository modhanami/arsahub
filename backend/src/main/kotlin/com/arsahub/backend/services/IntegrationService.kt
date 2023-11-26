package com.arsahub.backend.services

import com.arsahub.backend.dtos.IntegrationCreateRequest
import com.arsahub.backend.dtos.TriggerCreateRequest
import com.arsahub.backend.dtos.TriggerResponse
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.models.ExternalSystem
import com.arsahub.backend.models.Trigger
import com.arsahub.backend.repositories.ExternalSystemRepository
import com.arsahub.backend.repositories.TriggerRepository
import com.arsahub.backend.repositories.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class IntegrationService(
    private val triggerRepository: TriggerRepository,
    private val externalSystemRepository: ExternalSystemRepository,
    private val userRepository: UserRepository
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

    fun createIntegration(request: IntegrationCreateRequest): ExternalSystem {
        val user = userRepository.findById(request.createdBy).orElseThrow { Exception("User not found") }

        // name should be unique for each user
        externalSystemRepository.findByTitleAndCreatedBy(request.name, user)?.let {
            throw ConflictException("Integration with this name already exists")
        }

        val integration = ExternalSystem(
            title = request.name,
            createdBy = user,
            apiKey = UUID.randomUUID()
        )
        return externalSystemRepository.save(integration)
    }

    fun listIntegrations(userId: Long): List<ExternalSystem> {
        val user = userRepository.findById(userId).orElseThrow { Exception("User not found") }
        return externalSystemRepository.findAllByCreatedBy(user)
    }
}


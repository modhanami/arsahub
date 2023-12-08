package com.arsahub.backend.services

import com.arsahub.backend.dtos.AppCreateRequest
import com.arsahub.backend.dtos.AppWithAPIToken
import com.arsahub.backend.dtos.TriggerCreateRequest
import com.arsahub.backend.dtos.TriggerResponse
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppTemplate
import com.arsahub.backend.models.Trigger
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.AppTemplateRepository
import com.arsahub.backend.repositories.TriggerRepository
import com.arsahub.backend.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class AppService(
    private val triggerRepository: TriggerRepository,
    private val userRepository: UserRepository,
    private val appTemplateRepository: AppTemplateRepository,
    private val appRepository: AppRepository,
    private val apiKeyService: APIKeyService
) {

    fun createTrigger(request: TriggerCreateRequest): TriggerResponse {
        // TODO: enforce unique key per app
        val app = appRepository.findById(request.appId!!)
            .orElseThrow { Exception("App not found") }

        val trigger = Trigger(
            title = request.title,
            description = request.description,
            key = request.key,
            app = app
        )
        return TriggerResponse.fromEntity(triggerRepository.save(trigger))
    }

    fun getTriggers(appId: Long): List<Trigger> {
        return triggerRepository.findAllByAppId(appId)
    }

    fun createApp(request: AppCreateRequest): AppWithAPIToken {
        val user = userRepository.findById(request.createdBy).orElseThrow { Exception("User not found") }

        // name should be unique for each user
        appRepository.findByTitleAndCreatedBy(request.name, user)?.let {
            throw ConflictException("App with this name already exists for this user")
        }

        val generatedAPIKey = apiKeyService.generateKey()
        val app = App(
            title = request.name,
            createdBy = user,
            apiKey = generatedAPIKey.hashedAPIKey
        )
        val savedApp = appRepository.save(app)

        if (request.templateId == null) {
            return AppWithAPIToken(savedApp, generatedAPIKey.apiKey)
        }

        println("Populating for template ${request.templateId}")
        val template =
            appTemplateRepository.findById(request.templateId).orElseThrow { Exception("Template not found") }
        template.triggerTemplates.forEach {
            val trigger = Trigger(
                title = it.title,
                description = it.description,
                key = it.key,
                app = savedApp
            )
            triggerRepository.save(trigger)
            println("Created trigger ${it.title} for app ${savedApp.title} (${savedApp.id})")
        }

        return AppWithAPIToken(savedApp, generatedAPIKey.apiKey)
    }

    fun listApps(userId: Long): List<App> {
        val user = userRepository.findById(userId).orElseThrow { Exception("User not found") }
        return appRepository.findAllByCreatedBy(user)
    }

    fun listAppTemplates(): List<AppTemplate> {
        return appTemplateRepository.findAll()
    }
}


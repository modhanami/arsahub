package com.arsahub.backend.services

import com.arsahub.backend.dtos.*
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.models.*
import com.arsahub.backend.repositories.*
import org.springframework.stereotype.Service
import java.util.*

@Service
class AppService(
    private val triggerRepository: TriggerRepository,
    private val userRepository: UserRepository,
    private val appTemplateRepository: AppTemplateRepository,
    private val appRepository: AppRepository,
    private val apiKeyService: APIKeyService,
    private val appUserRepository: AppUserRepository

) {

    fun createTrigger(app: App, request: TriggerCreateRequest): TriggerResponse {
        val existingApp = app.id?.let { appRepository.findById(it).orElseThrow { Exception("App not found") } }
            ?: throw Exception("App not found")

        val trigger = Trigger(
            title = request.title,
            description = request.description,
            key = request.key,
            app = existingApp
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

    fun listAppTemplates(): List<AppTemplate> {
        return appTemplateRepository.findAll()
    }

    fun getAppByUserUUID(uuid: UUID): App {
        return appRepository.findByCreatedByUuid(uuid).first() ?: throw Exception("App not found")
    }

    fun getUserByUUID(userUUID: UUID): User {
        return userRepository.findByUuid(userUUID) ?: throw Exception("User not found")
    }

    fun addUser(app: App, request: AppUserCreateRequest): AppUser {
        val appUser = appUserRepository.findByAppAndUserId(app, request.uniqueId)
        if (appUser != null) {
            throw ConflictException("App user already exists")
        }
        val newAppUser = AppUser(
            userId = request.uniqueId,
            displayName = request.displayName,
            app = app
        )
        appUserRepository.save(newAppUser)
        return newAppUser
    }
}

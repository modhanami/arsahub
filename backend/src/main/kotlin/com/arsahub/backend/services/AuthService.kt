package com.arsahub.backend.services

import com.arsahub.backend.dtos.supabase.SupabaseIdentity
import com.arsahub.backend.models.App
import com.arsahub.backend.models.User
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val apiKeyService: APIKeyService,
    private val appRepository: AppRepository,
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun syncSupabaseIdentity(identity: SupabaseIdentity): User {
        val identityIdsMessage =
            "Supabase user ID ${identity.supabaseUserId} and Google user ID ${identity.googleUserId}"

        logger.info { "Syncing user data for $identityIdsMessage" }
        val user = userRepository.findByExternalUserId(identity.supabaseUserId)

        return if (user == null) {
            logger.info { "User not found for $identityIdsMessage, creating user" }
            val newUser =
                userRepository.save(
                    User(
                        externalUserId = identity.supabaseUserId,
                        googleUserId = identity.googleUserId,
                        email = identity.email,
                        name = identity.name,
                    ),
                )

            createAppForUser(newUser) // Default app

            newUser
        } else {
            logger.info { "User ${user.userId} found for $identityIdsMessage, updating user" }
            user.googleUserId = identity.googleUserId
            user.email = identity.email
            user.name = identity.name
            userRepository.save(user)
        }
    }

    private fun createAppForUser(user: User): App {
        logger.info { "Creating default app for user ${user.userId}" }
        val newApp =
            App(
                title = if (user.name != null) "${user.name}'s App" else "My App",
                owner = user,
                apiKey = apiKeyService.generateAPIKey(),
            )
        logger.info { "Created app ${newApp.id} for user ${user.userId}" }
        return appRepository.save(newApp)
    }
}

package com.arsahub.backend.security.auth

import com.arsahub.backend.models.User
import com.arsahub.backend.repositories.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class ExternalUserAuthenticationProvider(
    private val userRepository: UserRepository,
) : AuthenticationProvider {
    private val logger = KotlinLogging.logger {}

    override fun authenticate(authentication: Authentication): Authentication {
        val authenticationToken = authentication as ExternalUserAuthenticationToken
        val identity = authenticationToken.identity
        var user = userRepository.findByExternalUserId(identity.id)

        // auto create user if not exists
        // TODO: evaluate if this is the right approach
        if (user == null) {
            logger.info { "User not found for identity ${identity.id}, auto creating new user" }
            val newUser =
                User(
                    externalUserId = identity.id,
                )
            user = userRepository.save(newUser)
        } else {
            logger.info { "User ${user.userId} found for identity ${identity.id}" }
        }

        return ExternalUserAuthenticationToken.authenticated(
            authenticationToken.sessionCookie,
            identity,
            user,
        )
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == ExternalUserAuthenticationToken::class.java
    }
}

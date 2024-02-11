package com.arsahub.backend.security.auth

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

    override fun authenticate(authentication: Authentication): Authentication? {
        val authenticationToken = authentication as ExternalUserAuthenticationToken
        val identity = authenticationToken.identity

        val user = userRepository.findByExternalUserId(identity.id)
        if (user == null) {
            logger.info { "User not found for identity ${identity.id}" }
            return null
        }

        return ExternalUserAuthenticationToken.authenticated(
            identity,
            user,
        )
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == ExternalUserAuthenticationToken::class.java
    }
}

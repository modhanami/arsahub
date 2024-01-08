package com.arsahub.backend.security.auth

import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.services.APIKeyService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class AppAuthenticationProvider(
    private val appRepository: AppRepository,
    private val apiKeyService: APIKeyService,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val apiKey = authentication.credentials.toString()
        val app = appRepository.findByApiKey(apiKey)
        if (app == null || !apiKeyService.validateKeyForApp(app, apiKey)) {
            throw BadCredentialsException("The API key is invalid.")
        }
        return AppAuthenticationToken.authenticated(apiKey, app)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == AppAuthenticationToken::class.java
    }
}

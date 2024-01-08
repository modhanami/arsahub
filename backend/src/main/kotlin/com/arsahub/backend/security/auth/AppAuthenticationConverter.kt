package com.arsahub.backend.security.auth

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationConverter

class AppAuthenticationConverter : AuthenticationConverter {
    override fun convert(request: HttpServletRequest): Authentication {
        val apiKey =
            request.getHeader("X-API-Key")
                ?: throw BadCredentialsException("No API key found in request.")
        return AppAuthenticationToken.unauthenticated(apiKey)
    }
}

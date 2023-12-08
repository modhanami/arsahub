package com.arsahub.backend.security.auth

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationConverter

class AppAuthenticationConverter : AuthenticationConverter {
    override fun convert(request: HttpServletRequest): Authentication {
        val authHeader = request.getHeader("Authorization")
            ?: throw BadCredentialsException("No API key found in request.")
        if (!authHeader.startsWith("Bearer ")) {
            throw BadCredentialsException("The token type should be Bearer.")
        }
        val apiKey = authHeader.removePrefix("Bearer ")
        return AppAuthenticationToken.unauthenticated(apiKey)
    }
}
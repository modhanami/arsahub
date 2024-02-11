package com.arsahub.backend.security.auth

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationConverter

class ExternalUserAuthenticationConverter(
    private val oryService: OryService,
) : AuthenticationConverter {
    private val logger = KotlinLogging.logger {}

    override fun convert(request: HttpServletRequest): Authentication {
        val session = oryService.getSession(request.cookies.toList())
        val identity = session.identity

        requireNotNull(identity) { "No identity found in session" }

        return ExternalUserAuthenticationToken.unauthenticated(
            identity,
        )
    }
}

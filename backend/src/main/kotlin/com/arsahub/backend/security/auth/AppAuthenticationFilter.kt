package com.arsahub.backend.security.auth

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.web.authentication.AuthenticationFilter

class AppAuthenticationFilter(
    authenticationConfiguration: AuthenticationConfiguration
) : AuthenticationFilter(
    authenticationConfiguration.authenticationManager,
    AppAuthenticationConverter()
) {
    init {
        setSuccessHandler { _, _, _ -> }
    }
}


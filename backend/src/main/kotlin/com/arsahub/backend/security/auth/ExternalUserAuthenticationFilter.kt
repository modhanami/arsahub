package com.arsahub.backend.security.auth

import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.web.authentication.AuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.stereotype.Component

@Component
class ExternalUserAuthenticationFilter(
    authenticationManager: AuthenticationManager,
    oryService: OryService,
) : AuthenticationFilter(
        authenticationManager,
        ExternalUserAuthenticationConverter(oryService),
    ) {
    init {
        setSuccessHandler { _, _, _ -> }

        // Include only the endpoints that should be authenticated with the external user
        val matchers: List<RequestMatcher> =
            listOf(
                AntPathRequestMatcher("/api/apps/me", HttpMethod.GET.toString()),
            )

        setRequestMatcher(OrRequestMatcher(matchers))
    }
}

package com.arsahub.backend.security.auth

import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.web.authentication.AuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

class AppAuthenticationFilter(
    authenticationConfiguration: AuthenticationConfiguration
) : AuthenticationFilter(
    authenticationConfiguration.authenticationManager,
    AppAuthenticationConverter()
) {
    init {
        setSuccessHandler { _, _, _ -> }

        // Exclude the below paths from authentication
        val matchers: List<RequestMatcher> = listOf(
            AntPathRequestMatcher("/api/activities/**/leaderboard"),
            AntPathRequestMatcher("/api/activities/**/profile"),
            AntPathRequestMatcher("/api/apps", HttpMethod.GET.toString()),
            AntPathRequestMatcher("/api/apps/users/current", HttpMethod.GET.toString()),
            AntPathRequestMatcher("/api/apps/*/users/*", HttpMethod.GET.toString()), // TODO: reevaluate this
            AntPathRequestMatcher("/api/apps/*/leaderboard", HttpMethod.GET.toString()), // TODO: reevaluate this
            AntPathRequestMatcher("/swagger-ui/**"),
            AntPathRequestMatcher("/v3/api-docs/**"),
        )

        setRequestMatcher(NegatedRequestMatcher(OrRequestMatcher(matchers)))
    }
}
package com.arsahub.backend.security.auth

import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.web.authentication.AuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.stereotype.Component

@Component
class AppAuthenticationFilter(
    authenticationManager: AuthenticationManager,
) : AuthenticationFilter(
        authenticationManager,
        AppAuthenticationConverter(),
    ) {
    init {
        setSuccessHandler { _, _, _ -> }

        // Exclude the below paths from authentication
        val matchers: List<RequestMatcher> =
            listOf(
                AntPathRequestMatcher("/api/activities/**/leaderboard"),
                AntPathRequestMatcher("/api/activities/**/profile"),
                AntPathRequestMatcher("/api/apps", HttpMethod.GET.toString()),
                AntPathRequestMatcher("/api/apps/me", HttpMethod.GET.toString()),
                AntPathRequestMatcher("/api/apps/users/current", HttpMethod.GET.toString()),
                AntPathRequestMatcher("/api/apps/*/users/*", HttpMethod.GET.toString()), // TODO: reevaluate this
                AntPathRequestMatcher("/api/apps/*/leaderboard", HttpMethod.GET.toString()), // TODO: reevaluate this
                AntPathRequestMatcher("/swagger-ui/**"),
                AntPathRequestMatcher("/v3/api-docs/**"),
                AntPathRequestMatcher("/v3/api-docs.yaml"),
                AntPathRequestMatcher("/api/auth/**"),
                AntPathRequestMatcher("/error"),
            )

        requestMatcher = NegatedRequestMatcher(OrRequestMatcher(matchers))
    }
}

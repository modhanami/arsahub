package com.arsahub.backend.security

import com.arsahub.backend.security.auth.AppAuthenticationFilter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.AuthorizationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val appAuthenticationFilter: AppAuthenticationFilter,
    @Qualifier("jwtDecoder") private val myJwtDecoder: JwtDecoder,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                // TODO: evaluate auth for iframes. Currently both Leaderboards and app user profiles are public.
                authorize("/api/apps/*/leaderboard", permitAll)
                authorize("/api/apps/*/users/*", permitAll)
                authorize("/swagger-ui/**", permitAll)
                authorize("/v3/api-docs/**", permitAll)
                authorize("/v3/api-docs.yaml", permitAll)
                authorize("/error", permitAll)

                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer {
                jwt {
                    jwtDecoder = myJwtDecoder
                }
            }
            csrf { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            addFilterBefore<AuthorizationFilter>(appAuthenticationFilter)
            cors { }
        }
        return http.build()
    }
}

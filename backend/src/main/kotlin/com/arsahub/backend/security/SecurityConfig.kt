package com.arsahub.backend.security

import com.arsahub.backend.security.auth.AppAuthenticationFilter
import com.arsahub.backend.security.auth.ExternalUserAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.AuthorizationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val appAuthenticationFilter: AppAuthenticationFilter,
    private val externalUserAuthenticationFilter: ExternalUserAuthenticationFilter,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests {
                it.anyRequest().permitAll()
            }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(externalUserAuthenticationFilter, AuthorizationFilter::class.java)
            .addFilterBefore(appAuthenticationFilter, AuthorizationFilter::class.java)
            .cors { }
        return http.build()
    }
}

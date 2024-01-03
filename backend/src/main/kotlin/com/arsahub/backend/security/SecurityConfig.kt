package com.arsahub.backend.security

import com.arsahub.backend.security.auth.AppAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.AuthorizationFilter
import org.springframework.web.servlet.handler.HandlerMappingIntrospector


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val authenticationConfiguration: AuthenticationConfiguration
) {

    @Bean
    fun filterChain(http: HttpSecurity, introspector: HandlerMappingIntrospector): SecurityFilterChain {
        val appAuthenticationFilter = AppAuthenticationFilter(authenticationConfiguration)

        http
            .authorizeHttpRequests {
                it.anyRequest().permitAll()
            }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(appAuthenticationFilter, AuthorizationFilter::class.java)
            .cors {
                it.configurationSource {
                    val cors = org.springframework.web.cors.CorsConfiguration()
                    cors.cors.allowedOriginPatterns = listOf("/api/**")
                    cors.allowedMethods = listOf("*")
                    cors.allowedHeaders = listOf("*")
                    cors.allowCredentials = true
                    cors
                }
            }
        return http.build()
    }

}

package com.arsahub.backend.security

import com.arsahub.backend.security.auth.AppAuthenticationFilter
import com.arsahub.backend.security.auth.AuthProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtTimestampValidator
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.AuthorizationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val authProperties: AuthProperties,
) {
    @Value("\${cors.allowed-origins}")
    private lateinit var allowedOrigins: List<String>

    @Value("\${cors.allowed-headers}")
    private lateinit var allowedHeaders: List<String>

    @Value("\${cors.allowed-methods}")
    private lateinit var allowedMethods: List<String>

    @Value("\${cors.allow-credentials}")
    private var allowCredentials: Boolean = false

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val appAuthenticationFilter = AppAuthenticationFilter(authenticationConfiguration)

        http
            .authorizeHttpRequests {
                it.anyRequest().permitAll()
            }
            .oauth2ResourceServer { resourceServer ->
                resourceServer.jwt { jwt ->
                    jwt.decoder(jwtDecoder())
                }
            }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(appAuthenticationFilter, AuthorizationFilter::class.java)
            .cors { }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        // TODO: extract this to the config file
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = allowedOrigins
        configuration.allowedHeaders = allowedHeaders
        configuration.allowedMethods = allowedMethods
        configuration.allowCredentials = allowCredentials
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)

        return source
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val jwtDecoder = NimbusJwtDecoder.withSecretKey(authProperties.secretKey).build()
        val withoutClockSkew =
            DelegatingOAuth2TokenValidator(
                // TODO: check if this is still needed
                JwtTimestampValidator(java.time.Duration.ofSeconds(0)),
            )

        jwtDecoder.setJwtValidator(withoutClockSkew)
        return jwtDecoder
    }

    @Bean
    fun passwordEncoder(): Argon2PasswordEncoder {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()
    }
}

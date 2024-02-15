package com.arsahub.backend.security

import com.arsahub.backend.security.auth.AppAuthenticationProvider
import com.arsahub.backend.security.auth.AuthProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtTimestampValidator
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.time.Duration

@Configuration
@EnableWebSecurity
class SecurityBean(
    private val appAuthenticationProvider: AppAuthenticationProvider,
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
    fun providerManager(): AuthenticationManager {
        return ProviderManager(listOf(appAuthenticationProvider))
    }

    @Bean
    fun jwtDecoder(authProperties: AuthProperties): JwtDecoder {
        val jwtDecoder = NimbusJwtDecoder.withSecretKey(authProperties.secretKey).build()
        val withoutClockSkew =
            DelegatingOAuth2TokenValidator(
                // TODO: check if this is still needed
                JwtTimestampValidator(Duration.ofSeconds(0)),
            )

        jwtDecoder.setJwtValidator(withoutClockSkew)
        return jwtDecoder
    }
}

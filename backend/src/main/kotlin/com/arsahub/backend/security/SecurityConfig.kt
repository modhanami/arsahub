package com.arsahub.backend.security

import com.arsahub.backend.auth.APIKeyAuthFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.servlet.handler.HandlerMappingIntrospector


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
//    @Value("\${security.auth.jwt.private-key}") private val jwtPrivateKey: RSAPrivateKey,
//    @Value("\${security.auth.jwt.public-key}") private val jwtPublicKey: RSAPublicKey,
    @Value("\${arsahub.http.auth-token-header-name}")
    private val principalRequestHeader: String,

    @Value("\${arsahub.http.auth-token}")
    private val principalRequestValue: String,

    ) {
    @Bean
    fun filterChain(http: HttpSecurity, introspector: HandlerMappingIntrospector): SecurityFilterChain {
        val filter = APIKeyAuthFilter(principalRequestHeader)
        filter.setAuthenticationManager { authentication ->
            val principal = authentication.principal as String
            if (principalRequestValue != principal) {
                throw BadCredentialsException("The API key was not found or not the expected value.")
            }
            authentication.isAuthenticated = true
            authentication
        }
        http
            .authorizeHttpRequests {
//        and().addFilter(filter).authorizeRequests().anyRequest().authenticated();
                it.anyRequest().permitAll()
            }
            .addFilter(filter)
        csrf().disable().
            .csrf { it.disable() }
        sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
//            .httpBasic(Customizer.withDefaults())
//            .userDetailsService(customUserDetailsService)
        return http.build()
    }

//    @Bean
//    fun inMemUserDetailsService(): UserDetailsService {
//        val base = User.withDefaultPasswordEncoder()
//            .password("password")
//        val user = base
//            .username("user")
//            .roles("USER")
//            .build()
//        val admin = base
//            .username("admin")
//            .roles("ADMIN")
//            .build()
//        val organizer = base
//            .username("organizer")
//            .roles("ORGANIZER")
//            .build()
//        return InMemoryUserDetailsManager(user, admin, organizer)
//    }

}

//package com.arsahub.backend.security
//
//import com.arsahub.backend.services.CustomUserDetailsService
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.http.HttpMethod
//import org.springframework.security.config.Customizer
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
//import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
//import org.springframework.security.core.userdetails.User
//import org.springframework.security.core.userdetails.UserDetailsService
//import org.springframework.security.crypto.password.NoOpPasswordEncoder
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.security.provisioning.InMemoryUserDetailsManager
//import org.springframework.security.web.SecurityFilterChain
//import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
//import org.springframework.web.servlet.handler.HandlerMappingIntrospector
//import java.security.interfaces.RSAPrivateKey
//import java.security.interfaces.RSAPublicKey
//
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//class SecurityConfig(
//    private val customUserDetailsService: CustomUserDetailsService,
////    @Value("\${security.auth.jwt.private-key}") private val jwtPrivateKey: RSAPrivateKey,
////    @Value("\${security.auth.jwt.public-key}") private val jwtPublicKey: RSAPublicKey,
//) {
//    @Bean
//    fun filterChain(http: HttpSecurity, introspector: HandlerMappingIntrospector): SecurityFilterChain {
//        val mvc = MvcRequestMatcher.Builder(introspector)
//        http
//            .authorizeHttpRequests {
//                it.requestMatchers(mvc.pattern("/error")).permitAll()
////                it.requestMatchers(mvcMatcherBuilder.pattern("/api/events/**")).hasRole("ORGANIZER")
////                it.requestMatchers(mvcMatcherBuilder.pattern("/api/organizers/**")).hasRole("ADMIN")
//                it.requestMatchers(mvc.pattern(
//                    HttpMethod.POST,
//                    "/api/events"
//                )).hasAnyRole("ORGANIZER", "ADMIN")
//                it.requestMatchers(mvc.pattern("/api/events/joined/**")).hasRole("USER")
//                it.requestMatchers(mvc.pattern("/api/events/**")).hasAnyRole("ORGANIZER", "ADMIN")
//                it.anyRequest().authenticated()
//            }
////            .oauth2ResourceServer {
////                it.jwt { }
////            }
//            .csrf { it.disable() }
//            .httpBasic(Customizer.withDefaults())
//            .userDetailsService(customUserDetailsService)
//        return http.build()
//    }
//
////    @Bean
////    fun inMemUserDetailsService(): UserDetailsService {
////        val base = User.withDefaultPasswordEncoder()
////            .password("password")
////        val user = base
////            .username("user")
////            .roles("USER")
////            .build()
////        val admin = base
////            .username("admin")
////            .roles("ADMIN")
////            .build()
////        val organizer = base
////            .username("organizer")
////            .roles("ORGANIZER")
////            .build()
////        return InMemoryUserDetailsManager(user, admin, organizer)
////    }
//
//    @Bean
//    fun userDetailsService(): UserDetailsService {
//        return customUserDetailsService
//    }
//
////    @Bean
////    fun jwtEncoder(): JwtEncoder {
////        val jwk = RSAKey.Builder(jwtPublicKey)
////            .privateKey(jwtPrivateKey)
////            .build()
////        val immutableJwkSource = ImmutableJWKSet<SecurityContext>(JWKSet(jwk))
////        return NimbusJwtEncoder(immutableJwkSource)
////    }
////
////    @Bean
////    fun passwordEncoder(): PasswordEncoder {
////        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()
////    }
//
//    @Bean
//    fun passwordEncoder(): PasswordEncoder {
//        return NoOpPasswordEncoder.getInstance()
//    }
//}

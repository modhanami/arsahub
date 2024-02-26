package com.arsahub.backend.services

import com.arsahub.backend.dtos.supabase.SupabaseIdentity
import com.arsahub.backend.dtos.supabase.UserIdentity
import com.arsahub.backend.repositories.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class SupabaseService(
    private val userRepository: UserRepository,
) {
    private val logger = KotlinLogging.logger {}

    fun convertToUserIdentity(jwt: Jwt): UserIdentity {
        val supabaseGoogleIdentity = convertToSupabaseIdentity(jwt)
        val supabaseUserId = supabaseGoogleIdentity.supabaseUserId
        val internalUser =
            userRepository.findByExternalUserId(supabaseUserId)

        requireNotNull(internalUser) { "User ID not found for Supabase user ID $supabaseUserId" }
        checkNotNull(internalUser.userId) { "User ID not found for Supabase user ID $supabaseUserId" }

        return UserIdentity(
            internalUserId = internalUser.userId!!,
            externalUserId = supabaseGoogleIdentity.supabaseUserId,
            googleUserId = supabaseGoogleIdentity.googleUserId,
            email = supabaseGoogleIdentity.email,
            name = supabaseGoogleIdentity.name,
        )
    }

    fun convertToSupabaseIdentity(jwt: Jwt): SupabaseIdentity {
        val claims = jwt.claims
        val appMetadata = claims["app_metadata"] as? Map<*, *>
        val userMetadata = claims["user_metadata"] as? Map<*, *>
        val supabaseUserId = claims["sub"] as String
        val email = userMetadata?.get("email") as? String ?: claims["email"] as String
        val name = userMetadata?.get("name") as? String ?: email
        logger.debug { "appMetadata: $appMetadata" }
        logger.debug { "userMetadata: $userMetadata" }
        val isGoogleUser = appMetadata?.get("provider") == "google"
        logger.info { "isGoogleUser: $isGoogleUser" }
        val googleUserId =
            if (isGoogleUser) {
                userMetadata?.get("sub") as? String
            } else {
                null
            }

        return SupabaseIdentity(
            supabaseUserId = supabaseUserId,
            googleUserId = googleUserId,
            email = email,
            name = name,
        )
    }
}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@AuthenticationPrincipal(expression = "@supabaseService.convertToSupabaseIdentity(#this)")
annotation class SupabaseIdentityPrincipal

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@AuthenticationPrincipal(expression = "@supabaseService.convertToUserIdentity(#this)")
annotation class SupabaseUserIdentityPrincipal

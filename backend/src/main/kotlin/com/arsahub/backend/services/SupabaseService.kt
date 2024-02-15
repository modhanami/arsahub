package com.arsahub.backend.services

import com.arsahub.backend.dtos.supabase.SupabaseGoogleIdentity
import com.arsahub.backend.dtos.supabase.UserIdentity
import com.arsahub.backend.repositories.UserRepository
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class SupabaseService(
    private val userRepository: UserRepository,
) {
    fun convertToUserIdentity(jwt: Jwt): UserIdentity {
        val supabaseGoogleIdentity = convertToGoogleIdentity(jwt)
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

    fun convertToGoogleIdentity(jwt: Jwt): SupabaseGoogleIdentity {
        val claims = jwt.claims
        val userMetadata = claims["user_metadata"] as Map<*, *>
        val supabaseUserId = claims["sub"] as String

        return SupabaseGoogleIdentity(
            supabaseUserId = supabaseUserId,
            googleUserId = userMetadata["sub"] as String,
            email = userMetadata["email"] as String,
            name = userMetadata["name"] as String,
        )
    }
}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@AuthenticationPrincipal(expression = "@supabaseService.convertToGoogleIdentity(#this)")
annotation class SupabaseGoogleIdentityPrincipal

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@AuthenticationPrincipal(expression = "@supabaseService.convertToUserIdentity(#this)")
annotation class SupabaseUserIdentityPrincipal

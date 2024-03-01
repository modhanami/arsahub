package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.response.UserResponse
import com.arsahub.backend.dtos.supabase.SupabaseIdentity
import com.arsahub.backend.services.AuthService
import com.arsahub.backend.services.SupabaseIdentityPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    // Sync Supabase user data with our database
    // TODO: evaluate if this should be replaced with a webhook
    @PostMapping("/sync/supabase")
    fun syncSupabase(
        @SupabaseIdentityPrincipal identity: SupabaseIdentity,
    ): UserResponse {
        return authService.syncSupabaseIdentity(identity).let { UserResponse.fromEntity(it) }
    }
}

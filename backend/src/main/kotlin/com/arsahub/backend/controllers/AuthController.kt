package com.arsahub.backend.controllers

import com.arsahub.backend.services.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    // Sync Ory user data with our database
    // TODO: evaluate if this should be replaced with a webhook
    @PostMapping("/sync")
    fun sync(request: HttpServletRequest) {
        authService.syncOry(
            request,
        )
    }
}

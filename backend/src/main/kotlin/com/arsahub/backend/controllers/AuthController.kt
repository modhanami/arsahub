package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.request.UserLoginRequest
import com.arsahub.backend.dtos.request.UserSignupRequest
import com.arsahub.backend.dtos.response.LoginResponse
import com.arsahub.backend.dtos.response.SignupResponse
import com.arsahub.backend.services.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(@Valid @RequestBody request: UserSignupRequest): SignupResponse {
        val (newUser, _) = authService.createUser(request)
        val accessToken = authService.generateAccessToken(newUser)

        return SignupResponse(accessToken)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: UserLoginRequest): LoginResponse {
        val user = authService.authenticate(request)
        val accessToken = authService.generateAccessToken(user)

        return LoginResponse(accessToken)
    }

}
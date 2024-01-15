package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.request.UserLoginRequest
import com.arsahub.backend.dtos.request.UserSignupRequest
import com.arsahub.backend.dtos.response.LoginResponse
import com.arsahub.backend.dtos.response.SignupResponse
import com.arsahub.backend.exceptions.NotFoundException
import com.arsahub.backend.services.AuthService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.WebUtils

class RefreshTokenNotFoundException : NotFoundException("Refresh token not found")

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(
        @Valid @RequestBody request: UserSignupRequest,
    ): SignupResponse {
        val (newUser, _) = authService.createUser(request)
        val accessToken = authService.generateAccessToken(newUser)

        return SignupResponse(accessToken)
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: UserLoginRequest,
        response: HttpServletResponse,
    ): LoginResponse {
        val user = authService.authenticate(request)
        val accessToken = authService.generateAccessToken(user)
        val refreshToken = authService.generateRefreshToken(user)

        response.addCookie(makeRefreshTokenCookie(refreshToken))

        return LoginResponse(accessToken)
    }

    @PostMapping("/refresh")
    fun refresh(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): LoginResponse {
        val refreshToken =
            WebUtils.getCookie(request, "refreshToken")?.value ?: throw RefreshTokenNotFoundException()

        val user = authService.authenticateRefreshToken(refreshToken)
        val accessToken = authService.generateAccessToken(user)
        val newRefreshToken = authService.generateRefreshToken(user)

        response.addCookie(makeRefreshTokenCookie(newRefreshToken))

        return LoginResponse(accessToken)
    }

    fun makeRefreshTokenCookie(refreshToken: String): Cookie {
        return Cookie("refreshToken", refreshToken).apply {
            isHttpOnly = true
            secure = true
        }
    }

    fun unmakeRefreshTokenCookie(): Cookie {
        return Cookie("refreshToken", "").apply {
            maxAge = 0
        }
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse) {
        // TODO: revoke refresh token
        response.addCookie(unmakeRefreshTokenCookie())
    }
}

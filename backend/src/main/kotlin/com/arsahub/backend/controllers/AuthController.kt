package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.RegisterRequest
import com.arsahub.backend.dtos.LoginResponse
import com.arsahub.backend.services.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val userService: UserService){
    @PostMapping("/register")
    fun autoSignup(@RequestBody registerRequest: RegisterRequest): LoginResponse {
        return userService.register(registerRequest)
    }
}
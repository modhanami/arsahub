package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.LoginRequest
import com.arsahub.backend.dtos.LoginResponse
import com.arsahub.backend.services.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService){
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): LoginResponse {
        return userService.login(loginRequest)
    }
}
package com.arsahub.backend.dtos

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String
)
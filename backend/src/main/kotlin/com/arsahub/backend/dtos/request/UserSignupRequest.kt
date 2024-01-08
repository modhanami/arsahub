package com.arsahub.backend.dtos.request

import com.arsahub.backend.dtos.annotations.ValidPassword
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserSignupRequest(
    @field:NotBlank @field:Email
    val email: String,

    @ValidPassword
    val password: String,
)


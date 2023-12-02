package com.arsahub.backend.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class ActivityCreateRequest(
    @field:Size(min = 4, max = 200, message = "Title must be between 4 and 200 characters")
    @field:NotBlank(message = "Title is required")
    val title: String?,
    @field:Size(max = 500, message = "Description cannot be longer than 500 characters")
    val description: String?,
    @field:NotNull(message = "App ID is required")
    val appId: Long? // TODO: retrieve app ID from JWT
)
package com.arsahub.backend.dtos.request

import com.arsahub.backend.dtos.ValidationLengths
import com.arsahub.backend.dtos.ValidationMessages
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AppUserCreateRequest(
    @NotBlank(message = ValidationMessages.Constants.APP_USER_UID_REQUIRED)
    @Size(
        min = ValidationLengths.Constants.APP_USER_UID_MIN,
        max = ValidationLengths.Constants.APP_USER_UID_MAX,
        message = ValidationMessages.Constants.APP_USER_UID_LENGTH,
    )
    @Pattern(
        regexp = "^[a-zA-Z0-9_-]*\$",
        message = ValidationMessages.Constants.APP_USER_UID_PATTERN,
    )
    val uniqueId: String,
    @NotBlank(message = ValidationMessages.Constants.APP_USER_DISPLAY_NAME_REQUIRED)
    @Size(
        min = ValidationLengths.Constants.APP_USER_DISPLAY_NAME_MIN,
        max = ValidationLengths.Constants.APP_USER_DISPLAY_NAME_MAX,
        message = ValidationMessages.Constants.APP_USER_DISPLAY_NAME_LENGTH,
    )
    @Pattern(
        regexp = "^[a-zA-Z0-9_-]*\$",
        message = ValidationMessages.Constants.APP_USER_DISPLAY_NAME_PATTERN,
    )
    val displayName: String,
)

package com.arsahub.backend.dtos

data class AppUserCreateRequest(
    val uniqueId: String,
    val displayName: String
)
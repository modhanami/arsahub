package com.arsahub.backend.dtos.request

data class AppUserCreateRequest(
    val uniqueId: String,
    val displayName: String
)
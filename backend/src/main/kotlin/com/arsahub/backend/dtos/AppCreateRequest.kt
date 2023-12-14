package com.arsahub.backend.dtos

data class AppCreateRequest(
    val name: String,
    val createdBy: Long, // TODO: for testing purposes only, should be removed and retrieved from the session token, etc.
    val templateId: Long? = null
)
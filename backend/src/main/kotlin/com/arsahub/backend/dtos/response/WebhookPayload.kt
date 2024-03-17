package com.arsahub.backend.dtos.response

import java.util.*

data class WebhookPayload(
    val id: UUID,
    val event: String,
    val appUserId: String,
    val payload: Map<String, Any>,
)

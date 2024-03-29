package com.arsahub.backend.dtos.response

import com.arsahub.backend.models.Webhook

class WebhookResponse(
    val id: Long,
    val url: String,
    val secretKey: String? = null,
) {
    companion object {
        fun fromEntity(entity: Webhook): WebhookResponse {
            return WebhookResponse(
                id = entity.id!!,
                url = entity.url!!,
                secretKey = entity.secretKey,
            )
        }
    }
}

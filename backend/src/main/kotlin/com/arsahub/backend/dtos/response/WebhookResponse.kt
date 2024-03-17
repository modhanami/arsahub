package com.arsahub.backend.dtos.response

import com.arsahub.backend.models.Webhook

class WebhookResponse(
    val id: Long,
    val url: String,
) {
    companion object {
        fun fromEntity(entity: Webhook): WebhookResponse {
            return WebhookResponse(
                id = entity.id!!,
                url = entity.url!!,
            )
        }
    }
}

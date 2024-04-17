package com.arsahub.backend.dtos.response

import com.arsahub.backend.models.WebhookRequest

class WebhookRequestResponse(
    val id: Long,
    val webhookId: Long,
    val requestBody: MutableMap<String, Any>,
    val statusId: Long,
    val statusName: String,
    val signature: String,
    val appId: Long,
) {
    companion object {
        fun fromEntity(entity: WebhookRequest): WebhookRequestResponse {
            return WebhookRequestResponse(
                id = entity.id!!,
                webhookId = entity.webhook!!.id!!,
                requestBody = entity.requestBody!!,
                statusId = entity.status!!.id!!,
                statusName = entity.status!!.name!!,
                signature = entity.signature!!,
                appId = entity.app!!.id!!,
            )
        }
    }
}

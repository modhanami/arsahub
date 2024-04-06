package com.arsahub.backend.dtos.response

import java.util.*

class WebhookPayload(
    val id: UUID,
    val appId: Long,
    val webhookUrl: String,
    val event: String,
    val appUserId: String,
    val payload: Map<String, Any>,
) {
    override fun toString(): String {
        return "WebhookPayload(id=$id, appId='$appId', webhookUrl='$webhookUrl', event='$event', appUserId='$appUserId', payload=$payload)"
    }
}

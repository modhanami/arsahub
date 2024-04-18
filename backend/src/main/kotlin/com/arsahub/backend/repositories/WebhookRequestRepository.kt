package com.arsahub.backend.repositories

import com.arsahub.backend.models.App
import com.arsahub.backend.models.Webhook
import com.arsahub.backend.models.WebhookRequest
import com.arsahub.backend.models.WebhookRequestStatus
import org.springframework.data.jpa.repository.JpaRepository

interface WebhookRequestRepository : JpaRepository<WebhookRequest, Long> {
    fun findByAppAndWebhookAndStatusOrderByCreatedAtDesc(
        app: App,
        webhook: Webhook,
        status: WebhookRequestStatus,
    ): List<WebhookRequest>
}

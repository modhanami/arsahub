package com.arsahub.backend.models

import org.springframework.data.jpa.repository.JpaRepository

interface WebhookRepository : JpaRepository<Webhook, Long> {
    fun findByApp(app: App): List<Webhook>

    fun findByAppAndId(
        app: App,
        id: Long,
    ): Webhook?

    fun findByAppAndUrl(
        app: App,
        url: String,
    ): Webhook?
}

package com.arsahub.backend.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault

@Entity
@Table(name = "webhook_request_status")
class WebhookRequestStatus(
    @Column(name = "name", length = Integer.MAX_VALUE)
    var name: String? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('webhook_request_status_webhook_request_status_id_seq'")
    @Column(name = "webhook_request_status_id", nullable = false)
    var id: Long? = null
}

enum class WebhookRequestStatusEnum(val entity: WebhookRequestStatus) {
    SUCCESS(WebhookRequestStatus(name = "SUCCESS").apply { id = 1 }),
    FAILED(WebhookRequestStatus(name = "FAILED").apply { id = 2 }),
}

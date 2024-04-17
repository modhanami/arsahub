package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "webhook_request")
class WebhookRequest(
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "webhook_id", nullable = false)
    var webhook: Webhook? = null,
    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_body", nullable = false)
    var requestBody: MutableMap<String, Any>? = null,
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    var status: WebhookRequestStatus? = null,
    @NotNull
    @Column(name = "signature", nullable = false, length = Integer.MAX_VALUE)
    var signature: String? = null,
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ColumnDefault("1")
    @JoinColumn(name = "app_id", nullable = false)
    var app: App? = null,
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('webhook_request_webhook_request_id_seq'")
    @Column(name = "webhook_request_id", nullable = false)
    var id: Long? = null
}

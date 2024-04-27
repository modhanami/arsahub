package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.Where
import java.time.Instant

@Entity
@Table(name = "webhook", schema = "public")
@Where(clause = "deleted_at IS NULL")
class Webhook(
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_id", nullable = false)
    var app: App? = null,
    @NotNull
    @Column(name = "url", nullable = false, length = Integer.MAX_VALUE)
    var url: String? = null,
    @NotNull
    @Column(name = "secret_key", nullable = false, length = Integer.MAX_VALUE)
    var secretKey: String? = null,
    @Column(name = "deleted_at")
    var deletedAt: Instant? = null,
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "webhook_id", nullable = false)
    var id: Long? = null

    fun markAsDeleted() {
        deletedAt = Instant.now()
    }
}

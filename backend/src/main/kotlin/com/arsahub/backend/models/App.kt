package com.arsahub.backend.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "app")
class App(
    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    var title: String? = null,
    @Size(max = 255)
    @Column(name = "description")
    var description: String? = null,
    @NotNull
    @Column(name = "api_key", nullable = false)
    var apiKey: String? = null,
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    var owner: User? = null,
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_id", nullable = false)
    var id: Long? = null

    @Transient
    var webhook: String? = null
}

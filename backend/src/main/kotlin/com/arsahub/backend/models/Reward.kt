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
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "reward")
class Reward(
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_id", nullable = false)
    var app: App? = null,
    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    var name: String? = null,
    @Column(name = "description", length = Integer.MAX_VALUE)
    var description: String? = null,
    @NotNull
    @Column(name = "price", nullable = false)
    var price: Int? = null,
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_id", nullable = false)
    var type: RewardType? = null,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data")
    var data: MutableMap<String, Any>? = null,
    @Column(name = "quantity")
    var quantity: Int? = null,
    @Column(name = "image_key", length = Integer.MAX_VALUE)
    var imageKey: String? = null,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "image_metadata")
    var imageMetadata: MutableMap<String, Any>? = null,
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reward_id", nullable = false)
    var id: Long? = null
}

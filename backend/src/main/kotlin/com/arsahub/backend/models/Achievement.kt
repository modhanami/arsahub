package com.arsahub.backend.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "achievement")
class Achievement(
    @Column(name = "title", nullable = false)
    var title: String,
    @Column(name = "description")
    var description: String? = null,
    @OneToMany(mappedBy = "achievement")
    var appUserAchievements: MutableSet<AppUserAchievement> = mutableSetOf(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    var app: App? = null,
    @Column(name = "image_key", length = Integer.MAX_VALUE)
    var imageKey: String? = null,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "image_metadata")
    var imageMetadata: MutableMap<String, Any>? = null,
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_id")
    val achievementId: Long? = null
}

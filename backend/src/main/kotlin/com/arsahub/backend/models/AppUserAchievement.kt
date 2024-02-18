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
import java.time.Instant

@Entity
@Table(name = "app_user_achievement")
class AppUserAchievement(
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "achievement_id", nullable = false)
    var achievement: Achievement? = null,
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_user_id", nullable = false)
    var appUser: AppUser? = null,
    @Column(name = "completed_at")
    var completedAt: Instant? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    var app: App? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_user_achievement_id", nullable = false)
    var id: Long? = null
}

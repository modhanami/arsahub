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
import jakarta.validation.constraints.NotNull
import java.time.Instant

@Entity
@Table(name = "app_user")
class AppUser(
    @NotNull
    @Column(name = "unique_id", nullable = false, length = Integer.MAX_VALUE)
    var userId: String? = null,
    @NotNull
    @Column(name = "display_name", nullable = false, length = Integer.MAX_VALUE)
    var displayName: String? = null,
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_id", nullable = false)
    var app: App? = null,
    @NotNull
    @Column(name = "points", nullable = false)
    var points: Int? = null,
    @OneToMany(mappedBy = "appUser")
    var achievements: MutableSet<AppUserAchievement> = mutableSetOf(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null,
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_user_id", nullable = false)
    var id: Long? = null

    fun addPoints(points: Int) {
        this.points = this.points?.plus(points)
    }

    fun addAchievement(
        achievement: Achievement,
        completedAt: Instant,
    ) {
        val appUserAchievement =
            AppUserAchievement(app = this.app, appUser = this, achievement = achievement, completedAt = completedAt)
        achievements.add(appUserAchievement)
    }
}

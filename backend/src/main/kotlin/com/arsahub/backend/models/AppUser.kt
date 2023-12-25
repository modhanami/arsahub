package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

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
    var achievements: MutableSet<AppUserAchievement> = mutableSetOf()
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_user_id", nullable = false)
    var id: Long? = null

    fun addPoints(points: Int) {
        this.points = this.points?.plus(points)
    }

    fun addAchievement(achievement: Achievement) {
        val appUserAchievement = AppUserAchievement(appUser = this, achievement = achievement)
        achievements.add(appUserAchievement)
    }
}
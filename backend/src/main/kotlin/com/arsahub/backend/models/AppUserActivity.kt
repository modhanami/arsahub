package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(
    name = "app_user_activity", indexes = [
        Index(name = "idx_16425_user_id", columnList = "app_user_id"),
        Index(name = "idx_16425_activity_id", columnList = "activity_id")
    ]
)
class AppUserActivity(
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_user_id", nullable = false)
    var appUser: AppUser? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    var activity: Activity? = null,

    @NotNull
    @Column(name = "points", nullable = false)
    var points: Int? = 0,

    @OneToMany(mappedBy = "appUserActivity")
    var userActivityAchievements: MutableSet<UserActivityAchievement> = mutableSetOf(),

    @OneToMany(mappedBy = "appUserActivity")
    var userActivityProgresses: MutableSet<UserActivityProgress> = mutableSetOf()
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_user_activity_id", nullable = false)
    var id: Long? = null

    fun addPoints(points: Int) {
        this.points = this.points?.plus(points)
    }

    fun addAchievement(achievement: Achievement) {
        val userActivityAchievement = UserActivityAchievement(achievement, this)
        userActivityAchievements.add(userActivityAchievement)
    }
}
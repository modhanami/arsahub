package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.OffsetDateTime

@Entity
@Table(name = "app_user_activity_achievement")
class UserActivityAchievement(
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "achievement_id", nullable = false)
    var achievement: Achievement? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_user_activity_id", nullable = false)
    var appUserActivity: AppUserActivity? = null,

    @Column(name = "completed_at")
    var completedAt: OffsetDateTime? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_user_activity_achievement_id", nullable = false)
    var id: Long? = null
}
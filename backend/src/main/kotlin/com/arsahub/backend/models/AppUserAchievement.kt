package com.arsahub.backend.models

import jakarta.persistence.*
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
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_user_achievement_id", nullable = false)
    var id: Long? = null
}
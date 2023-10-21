package com.arsahub.backend.models

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "achievement_progress")
class AchievementProgress(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_progress_id")
    val achievementProgressId: Long = 0,

    @ManyToOne
    @JoinColumn(name = "achievement_id", nullable = false)
    val achievement: Achievement,

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Column(name = "progress", nullable = false)
    var progress: Int = 0,

    @Column(name = "completed_at")
    var completedAt: Instant? = null,
)
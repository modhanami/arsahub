package com.arsahub.backend.models

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "participation")
class Participation(
    @Id
    @Column(name = "participation_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    val event: Event,

    @Column(name = "points_earned", nullable = false)
    val pointsEarned: Int,

    @Column(nullable = false, name = "completed")
    val completed: Boolean,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, name = "completed_at")
    val completedAt: Instant
)
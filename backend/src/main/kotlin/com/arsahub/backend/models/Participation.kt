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

    @Column(name = "completed")
    var completed: Boolean = false,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "completed_at")
    var completedAt: Instant? = null,
) {
    fun markAsCompleted(completedAt: Instant) {
        completed = true
        this.completedAt = completedAt
    }
}
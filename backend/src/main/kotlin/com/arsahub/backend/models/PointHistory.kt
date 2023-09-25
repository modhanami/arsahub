package com.arsahub.backend.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(name = "point_history")
class PointHistory(
    @Id
    @Column(name = "point_history_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    val event: Event?,

    @Column(name = "points", nullable = false)
    val points: Int = 0,

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    val createdAt: Instant,

    @Column(name = "description")
    val description: String?
)

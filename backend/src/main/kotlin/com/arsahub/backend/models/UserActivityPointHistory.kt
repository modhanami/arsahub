package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(
    name = "user_activity_point_history", indexes = [
        Index(name = "idx_16432_user_activity_id", columnList = "user_activity_id"),
        Index(name = "idx_16432_activity_id", columnList = "activity_id")
    ]
)
class UserActivityPointHistory(
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_activity_id", nullable = false)
    var userActivity: UserActivity? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    var activity: Activity? = null,

    @NotNull
    @Column(name = "points", nullable = false)
    var points: Int? = null,

    @Size(max = 255)
    @Column(name = "description")
    var description: String? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_activity_point_history_id", nullable = false)
    var id: Long? = null
}
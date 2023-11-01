package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "rule_progress_times")
class RuleProgressTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_progress_single_id", nullable = false)
    var id: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rule_id", nullable = false)
    var rule: Rule? = null

    @NotNull
    @Column(name = "progress", nullable = false)
    var progress: Int? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_activity_id", nullable = false)
    var userActivity: UserActivity? = null

//    @NotNull
//    @Column(name = "created_at", nullable = false)
//    var createdAt: Instant? = null
}
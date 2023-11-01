package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.Instant

@Entity
@Table(name = "rule_progress_streak")
class RuleProgressStreak(
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rule_id", nullable = false)
    var rule: Rule? = null,

    @NotNull
    @Column(name = "current_streak", nullable = false)
    var currentStreak: Int? = null,

    @NotNull
    @Column(name = "last_interaction_at", nullable = false)
    var lastInteractionAt: Instant? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_activity_id", nullable = false)
    var userActivity: UserActivity? = null
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_progress_streak_id", nullable = false)
    var id: Long? = null


}
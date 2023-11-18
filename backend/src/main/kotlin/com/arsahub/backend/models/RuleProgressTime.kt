package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.Instant

@Entity
@Table(name = "rule_progress_times")
class RuleProgressTime(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_progress_single_id", nullable = false)
    var id: Long? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rule_id", nullable = false)
    var rule: Rule? = null,

    @NotNull
    @Column(name = "progress", nullable = false)
    var progress: Int? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_activity_id", nullable = false)
    var userActivity: UserActivity? = null,
    
    @Column(name = "completed_at")
    var completedAt: Instant? = null
) : AuditedEntity()
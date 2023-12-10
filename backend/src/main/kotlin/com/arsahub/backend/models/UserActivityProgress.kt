package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "app_user_activity_progress")
class UserActivityProgress(

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    var activity: Activity? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_user_activity_id", nullable = false)
    var appUserActivity: AppUserActivity? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_id", nullable = false)
    var customUnit: CustomUnit? = null,

    @NotNull
    @Column(name = "progress_value", nullable = false)
    var progressValue: Int? = null,
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_user_activity_progress_id", nullable = false)
    var id: Long? = null
}
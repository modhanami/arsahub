package com.arsahub.backend.models

import io.hypersistence.utils.hibernate.type.array.ListArrayType
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.Type

@Entity
@Table(name = "app_user_activity_progress")
class UserActivityProgress(

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_user_activity_id", nullable = false)
    var appUserActivity: AppUserActivity? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_id", nullable = false)
    var customUnit: CustomUnit? = null,

    @Column(name = "value_int")
    var valueInt: Int? = 0,

    @Column(name = "value_int_array")
    @Type(ListArrayType::class)
    var valueIntArray: MutableList<Int>? = null,
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_user_activity_progress_id", nullable = false)
    var id: Long? = null
}
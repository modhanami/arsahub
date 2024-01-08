package com.arsahub.backend.models

import io.hypersistence.utils.hibernate.type.array.ListArrayType
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.Type

@Entity
@Table(name = "app_user_progress")
class AppUserProgress(

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_user_id", nullable = false)
    var appUser: AppUser? = null,

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
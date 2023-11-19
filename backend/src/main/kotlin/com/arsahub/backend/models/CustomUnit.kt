package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "custom_unit")
class CustomUnit(
    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    var name: String? = null,

    @NotNull
    @Column(name = "key", nullable = false, length = Integer.MAX_VALUE)
    var key: String? = null,

    @OneToMany(mappedBy = "customUnit")
    var userActivityProgresses: MutableSet<UserActivityProgress> = mutableSetOf(),

    ) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id", nullable = false)
    var id: Long? = null

}
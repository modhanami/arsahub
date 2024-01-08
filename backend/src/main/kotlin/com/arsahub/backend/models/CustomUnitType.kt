package com.arsahub.backend.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "custom_unit_type")
class CustomUnitType(
    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    var name: String?,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_unit_type_id", nullable = false)
    var id: Long? = null
}

package com.arsahub.backend.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "rule_activation_type", schema = "public")
class RuleActivationType(
    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    var name: String? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_activation_type_id", nullable = false)
    var id: Long? = null
}

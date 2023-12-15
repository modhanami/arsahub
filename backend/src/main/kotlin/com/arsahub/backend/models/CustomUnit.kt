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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_id", nullable = false)
    var app: App? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "custom_unit_type_id", nullable = false)
    var type: CustomUnitType? = null,

    @OneToMany(mappedBy = "customUnit")
    var triggers: MutableSet<TriggerCustomUnit> = mutableSetOf(),

    ) : AuditedEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id", nullable = false)
    var id: Long? = null

}
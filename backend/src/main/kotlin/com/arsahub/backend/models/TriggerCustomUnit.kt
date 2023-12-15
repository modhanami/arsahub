package com.arsahub.backend.models

import jakarta.persistence.*

@Entity
@Table(name = "trigger_custom_unit", schema = "public")
class TriggerCustomUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trigger_custom_unit_id", nullable = false)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_unit_id")
    var customUnit: CustomUnit? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trigger_id")
    var trigger: Trigger? = null
}
package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "integration_template", schema = "public")
class IntegrationTemplate(

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    var name: String? = null,

    @Column(name = "description", length = Integer.MAX_VALUE)
    var description: String? = null,

    @OneToMany(mappedBy = "integrationTemplate")
    var triggerTemplates: MutableSet<TriggerTemplate> = mutableSetOf(),

    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "integration_template_id", nullable = false)
    var id: Long? = null
}
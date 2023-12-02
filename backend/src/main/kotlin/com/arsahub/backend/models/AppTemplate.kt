package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "app_template", schema = "public")
class AppTemplate(

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    var name: String? = null,

    @Column(name = "description", length = Integer.MAX_VALUE)
    var description: String? = null,

    @OneToMany(mappedBy = "appTemplate")
    var triggerTemplates: MutableSet<TriggerTemplate> = mutableSetOf(),

    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_template_id", nullable = false)
    var id: Long? = null
}
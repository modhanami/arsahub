package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "trigger_type")
class TriggerType(
    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    var title: String? = null,

    @Size(max = 255)
    @Column(name = "description")
    var description: String? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "json_schema")
    var jsonSchema: MutableMap<String, Any>? = null,

    @OneToMany(mappedBy = "triggerType")
    var rules: MutableSet<Rule> = mutableSetOf(),
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trigger_type_id", nullable = false)
    var id: Long? = null
}
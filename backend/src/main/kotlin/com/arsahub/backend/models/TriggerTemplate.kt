package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "trigger_template", schema = "public")
class TriggerTemplate(

    @NotNull
    @Column(name = "title", nullable = false, length = Integer.MAX_VALUE)
    var title: String? = null,

    @Column(name = "description", length = Integer.MAX_VALUE)
    var description: String? = null,

    @NotNull
    @Column(name = "key", nullable = false, length = Integer.MAX_VALUE)
    var key: String? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "json_schema")
    var jsonSchema: MutableMap<String, Any>? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_template_id", nullable = false)
    var appTemplate: AppTemplate? = null,

    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trigger_template_id", nullable = false)
    var id: Long? = null

}
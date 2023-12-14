package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "rule_template", schema = "public")
@AttributeOverrides(
    AttributeOverride(name = "updatedAt", column = Column(name = "updated_at", nullable = false))
)
class RuleTemplate(

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    var name: String? = null,

    @Column(name = "description", length = Integer.MAX_VALUE)
    var description: String? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trigger_id", nullable = false)
    var trigger: Trigger? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "action_id", nullable = false)
    var action: Action? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_id", nullable = false)
    var app: App? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "action_params")
    var actionParams: MutableMap<String, Any>? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "trigger_params")
    var triggerParams: MutableMap<String, Any>? = null

) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_template_id", nullable = false)
    var id: Long? = null

}
package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "rule")
class Rule(

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    var title: String? = null,

    @Size(max = 255)
    @Column(name = "description")
    var description: String? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trigger_id", nullable = false)
    var trigger: Trigger? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "trigger_params")
    var triggerParams: MutableMap<String, Any>? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    var app: App? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "conditions")
    var conditions: MutableMap<String, Any>? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rule_activation_type_id", nullable = false)
    var ruleActivationType: RuleActivationType? = null,

    @NotNull
    @Column(name = "action", nullable = false, length = Integer.MAX_VALUE)
    var action: String? = null,

    @Column(name = "action_points")
    var actionPoints: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_achievement_id")
    var actionAchievement: Achievement? = null,
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id", nullable = false)
    var id: Long? = null

}

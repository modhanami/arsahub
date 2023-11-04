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
    @JoinColumn(name = "activity_id", nullable = false)
    var activity: Activity? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trigger_id", nullable = false)
    var trigger: Trigger? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trigger_type_id")
    var triggerType: TriggerType? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "effect_id", nullable = false)
    var action: Action? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "trigger_type_params")
    var triggerTypeParams: MutableMap<String, Any>? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "effect_params")
    var actionParams: MutableMap<String, Any>? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "trigger_params")
    var triggerParams: MutableMap<String, Any>? = null
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id", nullable = false)
    var id: Long? = null

//    @OneToMany(mappedBy = "rule")
//    var ruleProgressStreaks: MutableSet<RuleProgressStreak> = mutableSetOf()
//
//    @OneToMany(mappedBy = "rule")
//    var ruleProgressTimes: MutableSet<RuleProgressTime> = mutableSetOf()
}

package com.arsahub.backend.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "`trigger`")
class Trigger(
    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    var title: String? = null,
    @Size(max = 255)
    @Column(name = "description")
    var description: String? = null,
    @OneToMany(mappedBy = "trigger")
    var rules: MutableSet<Rule> = mutableSetOf(),
    @NotNull
    @Column(name = "key", nullable = false, length = Integer.MAX_VALUE)
    var key: String? = null,
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    var app: App? = null,
    // TODO: evaluate if this is a good idea, among other queries
    @OneToMany(mappedBy = "trigger", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var fields: MutableSet<TriggerField> = mutableSetOf(),
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trigger_id", nullable = false)
    var id: Long? = null
}

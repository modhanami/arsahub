package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "activity")
class Activity(

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "description")
    var description: String?,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "external_system_id", nullable = false)
    var externalSystem: ExternalSystem? = null,

    @OneToMany(mappedBy = "activity")
    var rules: MutableSet<Rule> = mutableSetOf(),

    @OneToMany(mappedBy = "activity")
    var triggers: MutableSet<Trigger> = mutableSetOf(),

    @OneToMany(mappedBy = "activity")
    var members: MutableSet<UserActivity> = mutableSetOf(),

    @OneToMany(mappedBy = "activity")
    var userActivityPointHistories: MutableSet<UserActivityPointHistory> = mutableSetOf(),
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    val activityId: Long? = null

    override fun toString(): String {
        return "Event(activityId=$activityId, title='$title')"
    }
}
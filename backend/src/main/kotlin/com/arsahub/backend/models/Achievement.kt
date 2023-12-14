package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "achievement")
class Achievement(

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "image_url", length = Integer.MAX_VALUE)
    var imageUrl: String? = null,

    @OneToMany(mappedBy = "achievement")
    var userActivityAchievements: MutableSet<UserActivityAchievement> = mutableSetOf(),

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    var activity: Activity? = null
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_id")
    val achievementId: Long? = null

}
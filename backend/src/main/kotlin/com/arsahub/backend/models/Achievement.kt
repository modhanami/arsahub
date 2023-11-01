package com.arsahub.backend.models

import jakarta.persistence.*

@Entity
@Table(name = "achievement")
class Achievement(

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "required_progress", nullable = false)
    var requiredProgress: Int = 0,

//    @Column(name = "progress_type", nullable = false)
//    var progressType: String = "times"
    @Column(name = "image_url", length = Integer.MAX_VALUE)
    var imageUrl: String? = null,

    @OneToMany(mappedBy = "achievement")
    var userActivityAchievements: MutableSet<UserActivityAchievement> = mutableSetOf(),
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_id")
    val achievementId: Long? = null
}
package com.arsahub.backend.models

import jakarta.persistence.*

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
    var appUserAchievements: MutableSet<AppUserAchievement> = mutableSetOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    var app: App? = null
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_id")
    val achievementId: Long? = null

}
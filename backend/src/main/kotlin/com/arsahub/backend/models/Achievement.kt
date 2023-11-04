package com.arsahub.backend.models

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "achievement")
class Achievement(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_id")
    val achievementId: Long = 0,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "description")
    var description: String? = null,

    @OneToMany(mappedBy = "achievement")
    val achievementProgresses: MutableSet<AchievementProgress> = mutableSetOf(),

    @Column(name = "required_progress", nullable = false)
    var requiredProgress: Int = 0,

//    @Column(name = "progress_type", nullable = false)
//    var progressType: String = "times"
)
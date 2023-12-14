package com.arsahub.backend.repositories

import com.arsahub.backend.models.Achievement
import com.arsahub.backend.models.Activity
import org.springframework.data.jpa.repository.JpaRepository

interface AchievementRepository : JpaRepository<Achievement, Long> {
    fun findAllByActivity(activity: Activity): List<Achievement>
}
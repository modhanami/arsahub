package com.arsahub.backend.repositories

import com.arsahub.backend.models.Achievement
import com.arsahub.backend.models.App
import org.springframework.data.jpa.repository.JpaRepository

interface AchievementRepository : JpaRepository<Achievement, Long> {
    fun findAllByApp(app: App): List<Achievement>
}
package com.arsahub.backend.repositories

import com.arsahub.backend.models.Achievement
import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUserAchievement
import org.springframework.data.jpa.repository.JpaRepository

interface AppUserAchievementRepository : JpaRepository<AppUserAchievement, Long> {
    fun findAllByAchievementAndApp(
        achievement: Achievement,
        currentApp: App,
    ): List<AppUserAchievement>
}

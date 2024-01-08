package com.arsahub.backend.repositories

import com.arsahub.backend.models.AppUserAchievement
import org.springframework.data.jpa.repository.JpaRepository

interface AppUserAchievementRepository : JpaRepository<AppUserAchievement, Long>

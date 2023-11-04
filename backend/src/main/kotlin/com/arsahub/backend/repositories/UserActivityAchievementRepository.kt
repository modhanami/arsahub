package com.arsahub.backend.repositories

import com.arsahub.backend.models.UserActivityAchievement
import org.springframework.data.jpa.repository.JpaRepository

interface UserActivityAchievementRepository : JpaRepository<UserActivityAchievement, Long>
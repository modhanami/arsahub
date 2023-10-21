package com.arsahub.backend.repositories;

import com.arsahub.backend.models.AchievementProgress
import org.springframework.data.jpa.repository.JpaRepository

interface AchievementProgressRepository : JpaRepository<AchievementProgress, Long> {
}
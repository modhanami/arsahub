package com.arsahub.backend.repositories;

import com.arsahub.backend.models.Achievement
import org.springframework.data.jpa.repository.JpaRepository

interface AchievementRepository : JpaRepository<Achievement, Long> {
}
package com.arsahub.backend.repositories

import com.arsahub.backend.models.RuleProgressStreak
import org.springframework.data.jpa.repository.JpaRepository

interface RuleProgressStreakRepository : JpaRepository<RuleProgressStreak, Long>
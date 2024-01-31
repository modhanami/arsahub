package com.arsahub.backend.repositories

import com.arsahub.backend.models.RewardTemplate
import org.springframework.data.jpa.repository.JpaRepository

interface RewardTemplateRepository : JpaRepository<RewardTemplate, Long>

package com.arsahub.backend.repositories

import com.arsahub.backend.models.RuleActivationType
import org.springframework.data.jpa.repository.JpaRepository

interface RuleActivationTypeRepository : JpaRepository<RuleActivationType, Long>

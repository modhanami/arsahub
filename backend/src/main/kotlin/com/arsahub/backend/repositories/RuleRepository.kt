package com.arsahub.backend.repositories

import com.arsahub.backend.models.Rule
import org.springframework.data.jpa.repository.JpaRepository

interface RuleRepository : JpaRepository<Rule, Long>
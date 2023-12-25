package com.arsahub.backend.repositories

import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.RuleProgressTime
import org.springframework.data.jpa.repository.JpaRepository

interface RuleProgressTimeRepository : JpaRepository<RuleProgressTime, Long> {
    fun findByRuleAndAppUser(rule: Rule, appUserActivity: AppUser): RuleProgressTime?
}
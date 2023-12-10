package com.arsahub.backend.repositories

import com.arsahub.backend.models.AppUserActivity
import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.RuleProgressTime
import org.springframework.data.jpa.repository.JpaRepository

interface RuleProgressTimeRepository : JpaRepository<RuleProgressTime, Long> {

    //    fun findByRuleAndUserActivityAndCompletedAtIsNull(rule: Rule, appUserActivity: AppUserActivity): RuleProgressTime?
    fun findByRuleAndAppUserActivity(rule: Rule, appUserActivity: AppUserActivity): RuleProgressTime?
}
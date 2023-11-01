package com.arsahub.backend.repositories

import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.RuleProgressTime
import com.arsahub.backend.models.UserActivity
import org.springframework.data.jpa.repository.JpaRepository

interface RuleProgressTimeRepository : JpaRepository<RuleProgressTime, Long> {

    //    fun findByRuleAndUserActivityAndCompletedAtIsNull(rule: Rule, userActivity: UserActivity): RuleProgressTime?
    fun findByRuleAndUserActivity(rule: Rule, userActivity: UserActivity): RuleProgressTime?
}
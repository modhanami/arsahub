package com.arsahub.backend.repositories

import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.RuleProgress
import org.springframework.data.jpa.repository.JpaRepository

interface RuleProgressRepository : JpaRepository<RuleProgress, Long> {
    fun findByRuleAndAppAndAppUser(
        rule: Rule,
        app: App,
        appUser: AppUser,
    ): RuleProgress?

    fun findByRuleAndApp(
        rule: Rule,
        app: App,
    ): List<RuleProgress>
}

package com.arsahub.backend.repositories

import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.RuleTriggerFieldState
import com.arsahub.backend.models.TriggerField
import org.springframework.data.jpa.repository.JpaRepository

interface RuleTriggerFieldStateRepository : JpaRepository<RuleTriggerFieldState, Long> {
    fun findByAppAndAppUserAndRuleAndTriggerField(
        app: App,
        appUser: AppUser,
        rule: Rule,
        triggerField: TriggerField,
    ): RuleTriggerFieldState?
}

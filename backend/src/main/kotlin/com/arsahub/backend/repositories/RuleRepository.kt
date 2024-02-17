package com.arsahub.backend.repositories

import com.arsahub.backend.models.App
import com.arsahub.backend.models.Rule
import org.springframework.data.jpa.repository.JpaRepository

interface RuleRepository : JpaRepository<Rule, Long> {
    fun findAllByApp(app: App): List<Rule>

    fun findAllByAppAndTrigger_Key(
        app: App,
        key: String,
    ): List<Rule>
}

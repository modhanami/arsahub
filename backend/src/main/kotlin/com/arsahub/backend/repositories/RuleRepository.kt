package com.arsahub.backend.repositories

import com.arsahub.backend.models.App
import com.arsahub.backend.models.Rule
import org.springframework.data.jpa.repository.JpaRepository

interface RuleRepository : JpaRepository<Rule, Long> {
    fun findAllByApp(app: App): List<Rule>

    fun findAllByTrigger_Key(key: String): List<Rule>
}

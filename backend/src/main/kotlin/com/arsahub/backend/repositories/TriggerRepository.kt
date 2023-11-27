package com.arsahub.backend.repositories

import com.arsahub.backend.models.Trigger
import org.springframework.data.jpa.repository.JpaRepository

interface TriggerRepository : JpaRepository<Trigger, Long> {
    fun findByKey(key: String): Trigger?
    fun findAllByIntegrationId(integrationId: Long): List<Trigger>
}
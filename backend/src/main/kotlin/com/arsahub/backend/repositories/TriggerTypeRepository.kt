package com.arsahub.backend.repositories

import com.arsahub.backend.models.TriggerType
import org.springframework.data.jpa.repository.JpaRepository

interface TriggerTypeRepository : JpaRepository<TriggerType, Long> {
    fun findByTitle(name: String): TriggerType?
}
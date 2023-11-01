package com.arsahub.backend.repositories

import com.arsahub.backend.models.Action
import org.springframework.data.jpa.repository.JpaRepository

interface ActionRepository : JpaRepository<Action, Long> {
    fun findByKey(key: String): Action?
}
package com.arsahub.backend.repositories;

import com.arsahub.backend.models.CustomUnit
import org.springframework.data.jpa.repository.JpaRepository

interface CustomUnitRepository : JpaRepository<CustomUnit, Long> {
    fun findByKey(key: String): CustomUnit?
}
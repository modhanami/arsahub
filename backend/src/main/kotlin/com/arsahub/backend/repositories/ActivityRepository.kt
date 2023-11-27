package com.arsahub.backend.repositories

import com.arsahub.backend.models.Activity
import org.springframework.data.jpa.repository.JpaRepository

interface ActivityRepository : JpaRepository<Activity, Long> {
    fun findAllByExternalSystemId(integrationId: Long): List<Activity>
}
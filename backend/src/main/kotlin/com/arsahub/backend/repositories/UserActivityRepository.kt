package com.arsahub.backend.repositories

import com.arsahub.backend.models.AppUserActivity
import org.springframework.data.jpa.repository.JpaRepository

interface UserActivityRepository : JpaRepository<AppUserActivity, Long> {

    fun findAllByActivity_ActivityId(activitiyId: Long): List<AppUserActivity>
}
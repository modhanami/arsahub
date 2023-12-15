package com.arsahub.backend.repositories;

import com.arsahub.backend.models.AppUserActivity
import com.arsahub.backend.models.CustomUnit
import com.arsahub.backend.models.UserActivityProgress
import org.springframework.data.jpa.repository.JpaRepository

interface UserActivityProgressRepository : JpaRepository<UserActivityProgress, Long> {
    fun findByAppUserActivityAndCustomUnit(member: AppUserActivity, customUnit: CustomUnit): UserActivityProgress?
}
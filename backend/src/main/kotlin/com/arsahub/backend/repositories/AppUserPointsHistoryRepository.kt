package com.arsahub.backend.repositories

import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.AppUserPointsHistory
import org.springframework.data.jpa.repository.JpaRepository

interface AppUserPointsHistoryRepository : JpaRepository<AppUserPointsHistory, Long> {
    fun findAllByAppAndAppUser(
        app: App,
        appUser: AppUser,
    ): List<AppUserPointsHistory>

    fun findAllByAppAndAppUserOrderByCreatedAtDesc(
        app: App,
        appUser: AppUser,
    ): List<AppUserPointsHistory>
}

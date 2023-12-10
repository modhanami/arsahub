package com.arsahub.backend.repositories;

import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUser
import org.springframework.data.jpa.repository.JpaRepository

interface AppUserRepository : JpaRepository<AppUser, Long> {
    fun findByAppAndUserId(app: App, uniqueId: String): AppUser?
    fun findAllByAppAndUserIdIn(app: App, newMemberUserIds: List<String>): List<AppUser>
    fun findAllByApp(app: App): List<AppUser>
}
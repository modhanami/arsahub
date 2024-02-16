package com.arsahub.backend.repositories

import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppInvitation
import com.arsahub.backend.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface AppInvitationRepository : JpaRepository<AppInvitation, Long> {
    fun findByAppAndUser(
        app: App,
        user: User,
    ): AppInvitation?

    fun findByUserUserId(internalUserId: Long): AppInvitation?
}

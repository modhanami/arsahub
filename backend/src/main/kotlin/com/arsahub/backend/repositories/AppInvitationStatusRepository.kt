package com.arsahub.backend.repositories

import com.arsahub.backend.models.AppInvitationStatus
import org.springframework.data.jpa.repository.JpaRepository

interface AppInvitationStatusRepository : JpaRepository<AppInvitationStatus, Long> {
    fun findByStatusIgnoreCase(status: String): AppInvitationStatus?
}

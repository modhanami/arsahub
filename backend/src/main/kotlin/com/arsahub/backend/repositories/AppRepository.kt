package com.arsahub.backend.repositories

import com.arsahub.backend.models.App
import org.springframework.data.jpa.repository.JpaRepository

interface AppRepository : JpaRepository<App, Long> {
    fun findByApiKey(apiKey: String): App?

    fun findFirstByOwner_UserId(userId: Long): App?
}

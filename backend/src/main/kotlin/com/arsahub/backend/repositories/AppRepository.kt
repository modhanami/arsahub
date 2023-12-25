package com.arsahub.backend.repositories

import com.arsahub.backend.models.App
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AppRepository : JpaRepository<App, Long> {
    fun findByApiKey(apiKey: String): App?
    fun findFirstByOwnerUuid(uuid: UUID): App?
}
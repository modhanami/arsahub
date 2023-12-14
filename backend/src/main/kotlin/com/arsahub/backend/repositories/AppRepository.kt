package com.arsahub.backend.repositories;

import com.arsahub.backend.models.App
import com.arsahub.backend.models.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AppRepository : JpaRepository<App, Long> {
    fun findAllByCreatedBy(user: User?): List<App>
    fun findByTitleAndCreatedBy(name: String, user: User?): App?

    fun findByApiKey(apiKey: String): App?
    fun findByCreatedByUuid(uuid: UUID): List<App>
}
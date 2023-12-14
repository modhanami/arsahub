package com.arsahub.backend.repositories

import com.arsahub.backend.models.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun findByUuid(userUUID: UUID): User?
}
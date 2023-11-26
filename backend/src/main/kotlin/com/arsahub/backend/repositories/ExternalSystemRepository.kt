package com.arsahub.backend.repositories;

import com.arsahub.backend.models.ExternalSystem
import com.arsahub.backend.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface ExternalSystemRepository : JpaRepository<ExternalSystem, Long> {
    fun findAllByCreatedBy(user: User?): List<ExternalSystem>
    fun findByTitleAndCreatedBy(name: String, user: User?): ExternalSystem?
}
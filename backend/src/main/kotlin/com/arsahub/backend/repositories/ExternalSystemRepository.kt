package com.arsahub.backend.repositories;

import com.arsahub.backend.models.ExternalSystem
import org.springframework.data.jpa.repository.JpaRepository

interface ExternalSystemRepository : JpaRepository<ExternalSystem, Long> {
}
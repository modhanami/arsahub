package com.arsahub.backend.repositories;

import com.arsahub.backend.models.IntegrationTemplate
import org.springframework.data.jpa.repository.JpaRepository

interface IntegrationTemplateRepository : JpaRepository<IntegrationTemplate, Long> {
}
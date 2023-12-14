package com.arsahub.backend.repositories;

import com.arsahub.backend.models.AppTemplate
import org.springframework.data.jpa.repository.JpaRepository

interface AppTemplateRepository : JpaRepository<AppTemplate, Long> {
}
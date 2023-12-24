package com.arsahub.backend.repositories;

import com.arsahub.backend.models.TriggerLog
import org.springframework.data.jpa.repository.JpaRepository

interface TriggerLogRepository : JpaRepository<TriggerLog, Long>
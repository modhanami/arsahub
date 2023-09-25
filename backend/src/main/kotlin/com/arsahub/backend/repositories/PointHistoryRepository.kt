package com.arsahub.backend.repositories;

import com.arsahub.backend.models.PointHistory
import org.springframework.data.jpa.repository.JpaRepository

interface PointHistoryRepository : JpaRepository<PointHistory, Long>
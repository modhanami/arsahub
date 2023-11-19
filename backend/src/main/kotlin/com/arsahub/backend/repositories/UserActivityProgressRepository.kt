package com.arsahub.backend.repositories;

import com.arsahub.backend.models.UserActivityProgress
import org.springframework.data.jpa.repository.JpaRepository

interface UserActivityProgressRepository : JpaRepository<UserActivityProgress, Long>
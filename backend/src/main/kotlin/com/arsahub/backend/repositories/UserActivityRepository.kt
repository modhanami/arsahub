package com.arsahub.backend.repositories

import com.arsahub.backend.models.UserActivity
import org.springframework.data.jpa.repository.JpaRepository

interface UserActivityRepository : JpaRepository<UserActivity, Long>
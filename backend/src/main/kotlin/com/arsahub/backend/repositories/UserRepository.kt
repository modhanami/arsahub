package com.arsahub.backend.repositories;

import com.arsahub.backend.models.Event
import com.arsahub.backend.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>
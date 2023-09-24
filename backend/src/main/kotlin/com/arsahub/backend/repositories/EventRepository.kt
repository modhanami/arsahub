package com.arsahub.backend.repositories;

import com.arsahub.backend.models.Event
import org.springframework.data.jpa.repository.JpaRepository

interface EventRepository : JpaRepository<Event, Long>
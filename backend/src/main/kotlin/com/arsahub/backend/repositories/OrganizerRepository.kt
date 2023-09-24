package com.arsahub.backend.repositories

import com.arsahub.backend.models.Event
import com.arsahub.backend.models.Organizer
import org.springframework.data.jpa.repository.JpaRepository

interface OrganizerRepository : JpaRepository<Organizer, Long>
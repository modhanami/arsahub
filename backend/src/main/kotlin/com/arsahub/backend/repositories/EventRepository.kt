package com.arsahub.backend.repositories;

import com.arsahub.backend.models.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface EventRepository : JpaRepository<Event, Long> {

    @Query("select e from Event e where e.endTime < :endTime and e.completed = false")
    fun findUnfinishedEventsByEndTimeBefore(endTime: Instant): List<Event>
}
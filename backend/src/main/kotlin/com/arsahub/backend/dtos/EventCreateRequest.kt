package com.arsahub.backend.dtos

import com.arsahub.backend.models.Event
import java.time.Instant

data class EventCreateRequest(
    val title: String,
    val description: String?,
    val location: String?,
    val startTime: Instant,
    val endTime: Instant,
    val organizerId: Long,
    val points: Int
) {
    fun toEntity() = Event(
        title = title,
        description = description,
        location = location,
        startTime = startTime,
        endTime = endTime,
        organizerId = organizerId,
        points = points,
        completed = false,
    )
}
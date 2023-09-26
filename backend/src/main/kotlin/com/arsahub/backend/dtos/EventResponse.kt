package com.arsahub.backend.dtos

import com.arsahub.backend.models.Event
import java.time.Instant

data class EventResponse(
    val eventId: Long?,
    val title: String,
    val description: String?,
    val location: String?,
    val startTime: Instant,
    val endTime: Instant,
    val organizerId: Long,
    val points: Int,
) {
    companion object {
        fun fromEntity(event: Event): EventResponse {
            return EventResponse(
                eventId = event.eventId,
                title = event.title,
                description = event.description,
                location = event.location,
                startTime = event.startTime,
                endTime = event.endTime,
                organizerId = event.organizerId,
                points = event.points,
            )
        }
    }
}

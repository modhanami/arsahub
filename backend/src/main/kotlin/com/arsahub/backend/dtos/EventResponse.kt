package com.arsahub.backend.dtos

import com.arsahub.backend.models.Event
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

data class EventResponse(
    val eventId: Long?,
    val title: String,
    val description: String?,
    val location: String?,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,

    // ignore from JSON when null
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    val organizer: OrganizerResponse?,
) {
    companion object {
        fun fromEntity(event: Event): EventResponse {
            return EventResponse(
                eventId = event.eventId,
                title = event.title,
                description = event.description,
                location = event.location,
                startDate = event.startDate,
                endDate = event.endDate,
//                organizer = event.organizer?.let { OrganizerResponse.fromEntity(it) },
            )
        }
    }
}

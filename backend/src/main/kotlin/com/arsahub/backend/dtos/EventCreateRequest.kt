package com.arsahub.backend.dtos

import com.arsahub.backend.models.Event
import java.time.LocalDateTime

data class EventCreateRequest(
    val title: String,
    val description: String?,
    val location: String?,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val organizerId: Long
) {
    fun toEntity() = Event(
        title = title,
        description = description,
        location = location,
        startDate = startDate,
        endDate = endDate,
        organizerId = organizerId
    )
}
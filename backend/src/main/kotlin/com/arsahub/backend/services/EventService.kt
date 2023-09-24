package com.arsahub.backend.services

import com.arsahub.backend.dtos.EventCreateRequest
import com.arsahub.backend.dtos.EventResponse
import com.arsahub.backend.dtos.EventUpdateRequest
import com.arsahub.backend.models.Event

interface EventService {
    fun createEvent(eventCreateRequest: EventCreateRequest): EventResponse
    fun updateEvent(eventId: Long, eventUpdateRequest: EventUpdateRequest): EventResponse

    fun getEvent(eventId: Long): Event?
    fun listEvents(): List<Event>
    fun deleteEvent(eventId: Long)
    fun joinEvent(eventId: Long, userId: Long): Event
    fun listJoinedEvents(userId: Long): List<Event>
}


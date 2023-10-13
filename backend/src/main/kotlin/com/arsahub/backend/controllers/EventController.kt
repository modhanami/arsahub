package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.EventCreateRequest
import com.arsahub.backend.dtos.EventResponse
import com.arsahub.backend.dtos.EventUpdateRequest
import com.arsahub.backend.models.Event
import com.arsahub.backend.services.CustomUserDetails
import com.arsahub.backend.services.EventService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/events")
class EventController(private val eventService: EventService) {

    @PostMapping
    fun createEvent(
        @CurrentUser user: CustomUserDetails,
        @RequestBody eventCreateRequest: EventCreateRequest
    ): EventResponse {
        return eventService.createEvent(user, eventCreateRequest)
    }

    @PutMapping("/{eventId}")
    fun updateEvent(
        @CurrentUser user: CustomUserDetails,
        @PathVariable eventId: Long,
        @RequestBody eventUpdateRequest: EventUpdateRequest
    ): EventResponse {
        return eventService.updateEvent(user, eventId, eventUpdateRequest)
    }

    @GetMapping("/{eventId}")
    fun getEvent(@CurrentUser user: CustomUserDetails, @PathVariable eventId: Long): Event? {
        println(user)
        return eventService.getEvent(eventId)
    }

    @GetMapping
    fun listEvents(): List<Event> {
        return eventService.listEvents()
    }

    @DeleteMapping("/{eventId}")
    fun deleteEvent(@CurrentUser user: CustomUserDetails, @PathVariable eventId: Long) {
        eventService.deleteEvent(user, eventId)
    }

    @PostMapping("/{eventId}/join")
    fun joinEvent(@CurrentUser user: CustomUserDetails, @PathVariable eventId: Long): Event {
        return eventService.joinEvent(user, eventId)
    }

    @GetMapping("/joined")
    fun listJoinedEvents(@CurrentUser user: CustomUserDetails): List<Event> {
        return eventService.listJoinedEvents(user)
    }
}

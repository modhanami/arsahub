package com.arsahub.backend.controllers

import com.arsahub.backend.dtos.EventCreateRequest
import com.arsahub.backend.dtos.EventResponse
import com.arsahub.backend.dtos.EventUpdateRequest
import com.arsahub.backend.models.Event
import com.arsahub.backend.services.EventService
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/events")
class EventController(private val eventService: EventService) {

    @PostMapping
    fun createEvent(@RequestBody eventCreateRequest: EventCreateRequest): EventResponse {
        return eventService.createEvent(eventCreateRequest)
    }

    @PutMapping("/{eventId}")
    fun updateEvent(@PathVariable eventId: Long, @RequestBody eventUpdateRequest: EventUpdateRequest): EventResponse {
        return eventService.updateEvent(eventId, eventUpdateRequest)
    }

    @GetMapping("/{eventId}")
    fun getEvent(@PathVariable eventId: Long): Event? {
//        println("organizer: ${eventService.getEvent(eventId)?.organizer?.organizerId}")
        val event = eventService.getEvent(eventId)
        return Event(
            eventId = event?.eventId,
            title = "test",
            description = "test",
            location = "test",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now(),
            organizerId=1L,
        )
    }

    @GetMapping
    fun listEvents(): List<Event> {
        return eventService.listEvents()
    }

    @DeleteMapping("/{eventId}")
    fun deleteEvent(@PathVariable eventId: Long) {
        eventService.deleteEvent(eventId)
    }

    @PostMapping("/{eventId}/join")
    fun joinEvent(@PathVariable eventId: Long, @RequestParam userId: Long): Event {
        return eventService.joinEvent(eventId, userId)
    }

    @GetMapping("/joined")
    fun listJoinedEvents(@RequestParam userId: Long): List<Event> {
        return eventService.listJoinedEvents(userId)
    }
}

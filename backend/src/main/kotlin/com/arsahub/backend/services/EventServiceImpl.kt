package com.arsahub.backend.services

import com.arsahub.backend.dtos.EventCreateRequest
import com.arsahub.backend.dtos.EventResponse
import com.arsahub.backend.dtos.EventUpdateRequest
import com.arsahub.backend.models.Event
import com.arsahub.backend.models.Participation
import com.arsahub.backend.models.ParticipationRepository
import com.arsahub.backend.repositories.EventRepository
import com.arsahub.backend.repositories.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class EventServiceImpl(private val eventRepository: EventRepository, private val userRepository: UserRepository, private val participationRepository: ParticipationRepository) :
    EventService {

    override fun createEvent(eventCreateRequest: EventCreateRequest): EventResponse {
        val savedEvent = eventRepository.save(eventCreateRequest.toEntity())
        return EventResponse.fromEntity(savedEvent)
    }

    override fun updateEvent(eventId: Long, eventUpdateRequest: EventUpdateRequest): EventResponse {
        val existingEvent = eventRepository.findByIdOrNull(eventId)
            ?: throw EntityNotFoundException("Event with ID $eventId not found")

        val updatedEvent = updateEvent(existingEvent, eventUpdateRequest)

        val savedEvent = eventRepository.save(updatedEvent)
        return EventResponse.fromEntity(savedEvent)
    }

    fun updateEvent(existingEvent: Event, eventUpdateRequest: EventUpdateRequest): Event {
        if (eventUpdateRequest.title != null) {
            existingEvent.title = eventUpdateRequest.title
        }
        if (eventUpdateRequest.description != null) {
            existingEvent.description = eventUpdateRequest.description
        }
        if (eventUpdateRequest.location != null) {
            existingEvent.location = eventUpdateRequest.location
        }
        if (eventUpdateRequest.startDate != null) {
            existingEvent.startDate = eventUpdateRequest.startDate
        }
        if (eventUpdateRequest.endDate != null) {
            existingEvent.endDate = eventUpdateRequest.endDate
        }
        return existingEvent
    }

    override fun getEvent(eventId: Long): Event? {
        return eventRepository.findByIdOrNull(eventId)
    }

    override fun listEvents(): List<Event> {
        return eventRepository.findAll()
    }

    override fun deleteEvent(eventId: Long) {
        if (!eventRepository.existsById(eventId)) {
            throw EntityNotFoundException("Event with ID $eventId not found")
        }
        eventRepository.deleteById(eventId)
    }

    override fun joinEvent(eventId: Long, userId: Long): Event {
        val existingEvent = eventRepository.findByIdOrNull(eventId)
            ?: throw EntityNotFoundException("Event with ID $eventId not found")

        val existingUser = userRepository.findByIdOrNull(userId)
            ?: throw EntityNotFoundException("User with ID $userId not found")

        if (existingEvent.participations.any { it.user == existingUser }) {
            throw IllegalArgumentException("User with ID $userId already joined event with ID $eventId")
        }

        val participation = participationRepository.save(
            Participation(
                user = existingUser,
                event = existingEvent,
                pointsEarned = 0,
                completed = false,
                completedAt = Instant.now()
            )
        )

        existingEvent.participations.add(participation)

        return eventRepository.save(existingEvent)
    }

    override fun listJoinedEvents(userId: Long): List<Event> {
        val existingUser = userRepository.findByIdOrNull(userId)
            ?: throw EntityNotFoundException("User with ID $userId not found")

        return existingUser.participations.map { it.event }

    }
}
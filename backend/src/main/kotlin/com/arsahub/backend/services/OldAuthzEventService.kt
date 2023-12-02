//package com.arsahub.backend.services
//
//import com.arsahub.backend.dtos.EventCreateRequest
//import com.arsahub.backend.dtos.EventResponse
//import com.arsahub.backend.dtos.EventUpdateRequest
//import com.arsahub.backend.models.Event
//import com.arsahub.backend.models.Participation
//import com.arsahub.backend.repositories.EventRepository
//import com.arsahub.backend.repositories.ParticipationRepository
//import com.arsahub.backend.repositories.UserRepository
//import jakarta.persistence.EntityNotFoundException
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.data.repository.findByIdOrNull
//import org.springframework.security.access.prepost.PreAuthorize
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
//import org.springframework.security.core.userdetails.UserDetails
//import org.springframework.stereotype.Service
//import java.time.Instant
//
//interface EventService {
//    fun createEvent(eventCreateRequest: EventCreateRequest): EventResponse
//    fun updateEvent(activityId: Long, eventUpdateRequest: EventUpdateRequest): EventResponse
//
//    fun getEvent(activityId: Long): Event?
//    fun listEvents(): List<Event>
//    fun deleteEvent(activityId: Long)
//    fun joinEvent(activityId: Long, userId: Long): Event
//    fun listJoinedEvents(userId: Long): List<Event>
//    // check if an organizer is the organizer of an event
//}
//
//@Service
//@EnableMethodSecurity
//class EventServiceImpl(
//    private val eventRepository: EventRepository,
//    private val userRepository: UserRepository,
//    private val participationRepository: ParticipationRepository,
//) :
//    EventService {
//
//    @Autowired
//    lateinit var self: EventServiceImpl
//
//    override fun createEvent(eventCreateRequest: EventCreateRequest): EventResponse {
//        val savedEvent = eventRepository.save(eventCreateRequest.toEntity())
//        return EventResponse.fromEntity(savedEvent)
//    }
//
//    override fun updateEvent(activityId: Long, eventUpdateRequest: EventUpdateRequest): EventResponse {
//        println("updateEvent")
//        val existingEvent = eventRepository.findByIdOrNull(activityId)
//            ?: throw EntityNotFoundException("Event with ID $activityId not found")
//
//        return EventResponse.fromEntity(self.updateEventSecured(existingEvent, eventUpdateRequest))
//    }
//
//    @PreAuthorize("@eventServiceImpl.isOrganizerOfEvent(#existingEvent, principal)")
//    fun updateEventSecured(existingEvent: Event, eventUpdateRequest: EventUpdateRequest): Event {
//        println("updateEventSecured")
//        val updatedEvent = this@EventServiceImpl.updateEvent(existingEvent, eventUpdateRequest)
//        return eventRepository.save(updatedEvent)
//    }
//
//    fun isOrganizerOfEvent(event: Event, principal: UserDetails): Boolean {
//        println("isOrganizerOfEvent")
//        val name = event.organizer?.name
//        println("checking if ${principal.username} is organizer of event ${event.activityId} with organizer $name")
//        return name == principal.username
//    }
//
//
//    private fun updateEvent(existingEvent: Event, eventUpdateRequest: EventUpdateRequest): Event {
//        if (eventUpdateRequest.title != null) {
//            existingEvent.title = eventUpdateRequest.title
//        }
//        if (eventUpdateRequest.description != null) {
//            existingEvent.description = eventUpdateRequest.description
//        }
//        if (eventUpdateRequest.location != null) {
//            existingEvent.location = eventUpdateRequest.location
//        }
//        if (eventUpdateRequest.startTime != null) {
//            existingEvent.startTime = eventUpdateRequest.startTime
//        }
//        if (eventUpdateRequest.endTime != null) {
//            existingEvent.endTime = eventUpdateRequest.endTime
//        }
//        return existingEvent
//    }
//
//    override fun getEvent(activityId: Long): Event? {
//        return eventRepository.findByIdOrNull(activityId)
//    }
//
//    override fun listEvents(): List<Event> {
//        return eventRepository.findAll()
//    }
//
//    override fun deleteEvent(activityId: Long) {
//        if (!eventRepository.existsById(activityId)) {
//            throw EntityNotFoundException("Event with ID $activityId not found")
//        }
//        eventRepository.deleteById(activityId)
//    }
//
//    override fun joinEvent(activityId: Long, userId: Long): Event {
//        val existingEvent = eventRepository.findByIdOrNull(activityId)
//            ?: throw EntityNotFoundException("Event with ID $activityId not found")
//
//        val existingUser = userRepository.findByIdOrNull(userId)
//            ?: throw EntityNotFoundException("User with ID $userId not found")
//
//        if (existingEvent.participations.any { it.user == existingUser }) {
//            throw IllegalArgumentException("User with ID $userId already joined event with ID $activityId")
//        }
//
//        val participation = participationRepository.save(
//            Participation(
//                user = existingUser,
//                event = existingEvent,
//                completed = false,
//                completedAt = Instant.now()
//            )
//        )
//
//        existingEvent.participations.add(participation)
//
//        return eventRepository.save(existingEvent)
//    }
//
//    override fun listJoinedEvents(userId: Long): List<Event> {
//        val existingUser = userRepository.findByIdOrNull(userId)
//            ?: throw EntityNotFoundException("User with ID $userId not found")
//
//        return existingUser.participations.map { it.event }
//
//    }
//}
//
//

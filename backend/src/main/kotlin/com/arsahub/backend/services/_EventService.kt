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
//import org.springframework.data.repository.findByIdOrNull
//import org.springframework.security.access.AccessDeniedException
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
//import org.springframework.stereotype.Service
//import java.time.Instant
//
//interface EventService {
//    //    fun createEvent(eventCreateRequest: EventCreateRequest): EventResponse
//    // Support current user context
//    fun createEvent(currentUser: CustomUserDetails, eventCreateRequest: EventCreateRequest): EventResponse
//    fun updateEvent(
//        currentUser: CustomUserDetails,
//        activityId: Long,
//        eventUpdateRequest: EventUpdateRequest
//    ): EventResponse
//
//    fun getEvent(activityId: Long): Event?
//    fun listEvents(): List<Event>
//    fun deleteEvent(currentUser: CustomUserDetails, activityId: Long)
//    fun joinEvent(currentUser: CustomUserDetails, activityId: Long): Event
//    fun listJoinedEvents(currentUser: CustomUserDetails): List<Event>
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
//    override fun createEvent(currentUser: CustomUserDetails, eventCreateRequest: EventCreateRequest): EventResponse {
//        val eventToSave = Event(
//            title = eventCreateRequest.title,
//            description = eventCreateRequest.description,
//            location = eventCreateRequest.location,
//            startTime = eventCreateRequest.startTime,
//            endTime = eventCreateRequest.endTime,
//            points = eventCreateRequest.points,
//            completed = false,
//            organizerId = currentUser.userId
//        )
//        val savedEvent = eventRepository.save(eventToSave)
//        return EventResponse.fromEntity(savedEvent)
//    }
//
//    override fun updateEvent(
//        currentUser: CustomUserDetails,
//        activityId: Long,
//        eventUpdateRequest: EventUpdateRequest
//    ): EventResponse {
//        println("updateEvent")
//        val existingEvent = eventRepository.findByIdOrNull(activityId)
//            ?: throw EntityNotFoundException("Event with ID $activityId not found")
//
//        if (!canUpdateEvent(currentUser, existingEvent)) {
//            throw AccessDeniedException("Cannot update event with ID $activityId")
//        }
//
//        val updatedEvent = updateEvent(existingEvent, eventUpdateRequest)
//        return EventResponse.fromEntity(eventRepository.save(updatedEvent))
//    }
//
//    fun canUpdateEvent(currentUser: CustomUserDetails, event: Event): Boolean {
//        if (currentUser.isAdmin()) {
//            return true
//        }
//        return event.organizer?.organizerId == currentUser.userId
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
//    override fun deleteEvent(currentUser: CustomUserDetails, activityId: Long) {
//        if (!eventRepository.existsById(activityId)) {
//            throw EntityNotFoundException("Event with ID $activityId not found")
//        }
//        eventRepository.deleteById(activityId)
//    }
//
//    override fun joinEvent(currentUser: CustomUserDetails, activityId: Long): Event {
//        val existingEvent = eventRepository.findByIdOrNull(activityId)
//            ?: throw EntityNotFoundException("Event with ID $activityId not found")
//
//        val existingUser = userRepository.findByIdOrNull(currentUser.userId)
//            ?: throw EntityNotFoundException("User with ID ${currentUser.userId} not found")
//
//        if (existingEvent.participations.any { it.user == existingUser }) {
//            throw IllegalArgumentException("User with ID ${currentUser.userId} already joined event with ID $activityId")
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
//    override fun listJoinedEvents(currentUser: CustomUserDetails): List<Event> {
//        val existingUser = userRepository.findByIdOrNull(currentUser.userId)
//            ?: throw EntityNotFoundException("User with ID ${currentUser.userId} not found")
//
//        return existingUser.participations.map { it.event }
//
//    }
//}

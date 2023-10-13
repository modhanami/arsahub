package com.arsahub.backend.services

import com.arsahub.backend.dtos.EventCreateRequest
import com.arsahub.backend.dtos.EventUpdateRequest
import com.arsahub.backend.models.Event
import com.arsahub.backend.models.Organizer
import com.arsahub.backend.models.User
import com.arsahub.backend.repositories.EventRepository
import com.arsahub.backend.repositories.OrganizerRepository
import com.arsahub.backend.repositories.UserRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Instant

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test-containers")
class EventServiceImplRepositoryTest {
    @Autowired
    lateinit var eventService: EventService

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var organizerRepository: OrganizerRepository

    @Autowired
    lateinit var userRepository: UserRepository

    lateinit var organizer: Organizer

    @BeforeEach
    fun setup() {
        eventRepository.deleteAll()
        organizerRepository.deleteAll()
        organizer = organizerRepository.save(Organizer(name = "Test Organizer"))
    }

    @Test
    fun `createEvent should save the event`() {
        // Arrange
        val eventCreateRequest = EventCreateRequest(
            title = "Sample Event",
            description = "This is a test event",
            location = "Test Location",
            startTime = Instant.parse("2023-09-25T10:00:00Z"),
            endTime = Instant.parse("2023-09-26T16:00:00Z"),
            organizerId = organizer.organizerId,
            points = 100
        )

        // Act
        val result = eventService.createEvent(eventCreateRequest)

        // Assert
        assertEquals(eventCreateRequest.title, result.title)
        assertEquals(eventCreateRequest.description, result.description)
        assertEquals(eventCreateRequest.location, result.location)
        assertEquals(eventCreateRequest.startTime, result.startTime)
        assertEquals(eventCreateRequest.endTime, result.endTime)
        assertEquals(eventCreateRequest.organizerId, result.organizerId)
        assertEquals(eventCreateRequest.points, result.points)
    }

    @Test
    fun `updateEvent should update and return the event`() {
        // Arrange
        // Create an initial event
        val initialEvent = Event(
            title = "Initial Event",
            description = "This is the initial event",
            location = "Initial Location",
            startTime = Instant.parse("2023-09-25T10:00:00Z"),
            endTime = Instant.parse("2023-09-26T16:00:00Z"),
            organizerId = organizer.organizerId,
            points = 50 // Initial points
        )
        val savedInitialEvent = eventRepository.save(initialEvent)

        val eventId = savedInitialEvent.eventId // Get the ID of the initial event

        val eventUpdateRequest = EventUpdateRequest(
            title = "Updated Event",
            description = "This is an updated event",
            location = "Updated Location",
            startTime = Instant.parse("2023-09-27T10:00:00Z"),
            endTime = Instant.parse("2023-09-27T16:00:00Z")
        )

        // Act
        val result = eventService.updateEvent(eventId, eventUpdateRequest)

        // Assert
        assertEquals(eventUpdateRequest.title, result.title)
        assertEquals(eventUpdateRequest.description, result.description)
        assertEquals(eventUpdateRequest.location, result.location)
        assertEquals(eventUpdateRequest.startTime, result.startTime)
        assertEquals(eventUpdateRequest.endTime, result.endTime)
        // Add more assertions as needed
    }

    @Test
    fun `getEvent should return the event if it exists`() {
        // Arrange
        // Create an event to be fetched
        val eventToFetch = Event(
            title = "Event to Fetch",
            description = "This is the event to fetch",
            location = "Fetch Location",
            startTime = Instant.parse("2023-09-28T10:00:00Z"),
            endTime = Instant.parse("2023-09-28T16:00:00Z"),
            organizerId = organizer.organizerId,
            points = 75
        )
        val savedEventToFetch = eventRepository.save(eventToFetch)

        val eventId = savedEventToFetch.eventId // Get the ID of the event to fetch

        // Act
        val result = eventService.getEvent(eventId)!!

        // Assert
        assertEquals(savedEventToFetch.title, result.title)
        assertEquals(savedEventToFetch.description, result.description)
        assertEquals(savedEventToFetch.location, result.location)
        assertEquals(savedEventToFetch.startTime, result.startTime)
        assertEquals(savedEventToFetch.endTime, result.endTime)
        // Add more assertions as needed
    }

    @Test
    fun `getEvent should return null if the event does not exist`() {
        // Arrange
        val nonExistentEventId = -1L // A non-existent event ID

        // Act
        val result = eventService.getEvent(nonExistentEventId)

        // Assert
        assertNull(result)
    }

    @Test
    fun `listEvents should return a list of events`() {
        // Arrange
        // Create multiple events
        val event1 = Event(
            title = "Event 1",
            description = "This is event 1",
            location = "Location 1",
            startTime = Instant.parse("2023-09-28T10:00:00Z"),
            endTime = Instant.parse("2023-09-28T12:00:00Z"),
            organizerId = organizer.organizerId,
            points = 60
        )
        val event2 = Event(
            title = "Event 2",
            description = "This is event 2",
            location = "Location 2",
            startTime = Instant.parse("2023-09-29T10:00:00Z"),
            endTime = Instant.parse("2023-09-29T12:00:00Z"),
            organizerId = organizer.organizerId,
            points = 70
        )

        eventRepository.save(event1)
        eventRepository.save(event2)

        // Act
        val result = eventService.listEvents()

        // Assert
        assertEquals(2, result.size) // Check if the list contains two events (you can adjust based on your test data)
    }

    @Test
    fun `listEvents should return an empty list if no events exist`() {
        // Act
        val result = eventService.listEvents()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `deleteEvent should delete the event if it exists`() {
        // Arrange
        // Create an event to be deleted
        val eventToDelete = Event(
            title = "Event to Delete",
            description = "This is the event to delete",
            location = "Delete Location",
            startTime = Instant.parse("2023-09-30T10:00:00Z"),
            endTime = Instant.parse("2023-09-30T12:00:00Z"),
            organizerId = organizer.organizerId,
            points = 80
        )
        val savedEventToDelete = eventRepository.save(eventToDelete)

        val eventId = savedEventToDelete.eventId // Get the ID of the event to delete

        // Act
        eventService.deleteEvent(eventId)

        // Assert
        val deletedEvent = eventService.getEvent(eventId)
        assertNull(deletedEvent) // Check if the event no longer exists
    }

    @Test
    fun `deleteEvent should do nothing if the event does not exist`() {
        // Arrange
        val nonExistentEventId = -1L // A non-existent event ID

        // Act, Assert
        assertThrows(EntityNotFoundException::class.java) {
            eventService.deleteEvent(nonExistentEventId)
        }
    }

    @Test
    @Transactional
    fun `joinEvent should add a user to the event and return the updated event`() {
        // Arrange
        // Create an event
        val eventToJoin = Event(
            title = "Event to Join",
            description = "This is the event to join",
            location = "Join Location",
            startTime = Instant.parse("2023-10-01T10:00:00Z"),
            endTime = Instant.parse("2023-10-01T12:00:00Z"),
            organizerId = organizer.organizerId,
            points = 90
        )
        val savedEventToJoin = eventRepository.save(eventToJoin)

        val eventId = savedEventToJoin.eventId // Get the ID of the event to join

        // Create a user
        val userToJoin = User(username = "testuser")
        val savedUserToJoin = userRepository.save(userToJoin)

        val userId = savedUserToJoin.userId // Get the ID of the user to join

        // Act
        val result = eventService.joinEvent(eventId, userId)

        // Assert
        assertTrue(result.participations.any { it.user.userId == userId }) // Check if the user is in the event's participations
    }

    @Test
    @Transactional
    fun `joinEvent should throw an exception if the user already joined the event`() {
        // Arrange
        // Create an event
        val eventToJoin = Event(
            title = "Event to Join",
            description = "This is the event to join",
            location = "Join Location",
            startTime = Instant.parse("2023-10-02T10:00:00Z"),
            endTime = Instant.parse("2023-10-02T12:00:00Z"),
            organizerId = organizer.organizerId,
            points = 100
        )
        val savedEventToJoin = eventRepository.save(eventToJoin)

        val eventId = savedEventToJoin.eventId // Get the ID of the event to join

        // Create a user
        val userToJoin = User(username = "testuser")
        val savedUserToJoin = userRepository.save(userToJoin)

        val userId = savedUserToJoin.userId // Get the ID of the user to join

        // Join the user to the event once
        eventService.joinEvent(eventId, userId)

        // Act and Assert
        assertThrows(IllegalArgumentException::class.java) {
            // Attempt to join the same user to the same event again, which should throw an exception
            eventService.joinEvent(eventId, userId)
        }
    }

}
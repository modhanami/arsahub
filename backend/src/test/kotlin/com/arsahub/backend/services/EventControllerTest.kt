//package com.arsahub.backend.services
//
//import com.arsahub.backend.models.Event
//import com.arsahub.backend.repositories.EventRepository
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.mockito.Mockito.*
//import org.springframework.data.repository.findByIdOrNull
//import java.time.LocalDateTime
//
//object TestData {
//    fun createEvent(): Event {
//        return createEvent(null)
//    }
//
//    fun createEvent(id: Long?): Event {
//        return Event(
//            eventId = id,
//            title = "Sample Event",
//            description = "This is a test event",
//            location = "Test Location",
//            startDate = LocalDateTime.of(2023, 9, 25, 10, 0), // Replace with your desired date and time
//            endDate = LocalDateTime.of(2023, 9, 26, 16, 0),   // Replace with your desired date and time
//            organizerId = 1L // Replace with the ID of an existing organizer for testing
//        )
//    }
//
//    // You can create more event objects for different test cases if needed.
//}
//
//class EventServiceImplTest {
//
//    val eventRepository: EventRepository  = mockk()
//    val eventService: EventService = EventServiceImpl(eventRepository)
//
//    @Test
//    fun `getEvent should return an existing event`() {
//        // Arrange
//        val eventId = 1L
//        val existingEvent = TestData.createEvent(eventId)
//        every { eventRepository.findByIdOrNull(eventId) } returns existingEvent
//
//        // Act
//        val result = eventService.getEvent(eventId)
//
//        // Assert
//        verify(exactly = 1) { eventRepository.findById(eventId) }
//        assertEquals(existingEvent, result)
//    }
//
//    @Test
//    fun `createEvent should save the event`() {
//        // Arrange
//        val eventToCreate = TestData.createEvent()
//        val savedEvent = TestData.createEvent(1L)
//       every { eventRepository.save(eventToCreate) } returns savedEvent
//
//        // Act
//        val createdEvent = eventService.createEvent(eventToCreate)
//
//        // Assert
//        verify(exactly = 1) { eventRepository.save(eventToCreate) }
//        assertEquals(createdEvent, savedEvent)
//    }
//
//    @Test
//    fun `updateEvent should update the event`() {
//        // Arrange
//        val eventId = 1L
//        val existingEvent = TestData.createEvent(eventId)
//        val updatedEvent = TestData.createEvent(eventId)
//        every { eventRepository.findByIdOrNull(eventId) } returns existingEvent
//        every { eventRepository.save(existingEvent) } returns updatedEvent
//
//        // Act
//        val result = eventService.updateEvent(eventId, updatedEvent)
//
//        // Assert
//        verify(exactly = 1) { eventRepository.findByIdOrNull(eventId) }
//        verify(exactly = 1) { eventRepository.save(existingEvent) }
//        assertEquals(updatedEvent, result)
//    }
//
//    @Test
//    fun `listEvents should return a list of events`() {
//        // Arrange
//        val event1 = TestData.createEvent(1L)
//        val event2 = TestData.createEvent(2L)
//        val event3 = TestData.createEvent(3L)
//        val eventList = listOf(event1, event2, event3)
//        every { eventRepository.findAll() } returns eventList
//
//        // Act
//        val result = eventService.listEvents()
//
//        // Assert
//        verify(exactly = 1) { eventRepository.findAll() }
//        assertEquals(eventList, result)
//    }
//}

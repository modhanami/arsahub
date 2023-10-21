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
//            activityId = id,
//            title = "Sample Event",
//            description = "This is a test event",
//            location = "Test Location",
//            startTime = LocalDateTime.of(2023, 9, 25, 10, 0), // Replace with your desired date and time
//            endTime = LocalDateTime.of(2023, 9, 26, 16, 0),   // Replace with your desired date and time
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
//        val activityId = 1L
//        val existingEvent = TestData.createEvent(activityId)
//        every { eventRepository.findByIdOrNull(activityId) } returns existingEvent
//
//        // Act
//        val result = eventService.getEvent(activityId)
//
//        // Assert
//        verify(exactly = 1) { eventRepository.findById(activityId) }
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
//        val activityId = 1L
//        val existingEvent = TestData.createEvent(activityId)
//        val updatedEvent = TestData.createEvent(activityId)
//        every { eventRepository.findByIdOrNull(activityId) } returns existingEvent
//        every { eventRepository.save(existingEvent) } returns updatedEvent
//
//        // Act
//        val result = eventService.updateEvent(activityId, updatedEvent)
//
//        // Assert
//        verify(exactly = 1) { eventRepository.findByIdOrNull(activityId) }
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

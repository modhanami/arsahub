package com.arsahub.backend.schedules

import com.arsahub.backend.models.PointHistory
import com.arsahub.backend.repositories.EventRepository
import com.arsahub.backend.repositories.PointHistoryRepository
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class EventFinishScheduler(
    private val eventRepository: EventRepository,
    private val pointHistoryRepository: PointHistoryRepository
) {

    @Scheduled(fixedDelay = 5000, initialDelay = 0) // every 5 seconds
    @Transactional
    fun markFinishedEvents() {
        val currentTime = Instant.now()
        val finishedEvents = eventRepository.findUnfinishedEventsByEndTimeBefore(currentTime)

        if (finishedEvents.isNotEmpty()) {
            println("Finished ${finishedEvents.size} events (IDs: ${finishedEvents.map { it.eventId }})")
        }

        finishedEvents.map { event ->
            event.markAsCompleted(currentTime)
        }

        val pointHistories: List<PointHistory> = finishedEvents.flatMap { event ->
            println("event: ${event.eventId} has ${event.participations.size} participations")
            event.participations.map { participation ->
                participation.markAsCompleted(currentTime)
                PointHistory(
                    user = participation.user,
                    event = event,
                    points = event.points,
                    createdAt = currentTime,
                    description = "Participated in event ${event.title} (ID: ${event.eventId})"
                )
            }
        }

        pointHistoryRepository.saveAll(pointHistories)
        eventRepository.saveAll(finishedEvents)
    }
}
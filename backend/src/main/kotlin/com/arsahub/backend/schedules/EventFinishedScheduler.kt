//package com.arsahub.backend.schedules
//
//import com.arsahub.backend.models.PointHistory
//import com.arsahub.backend.repositories.EventRepository
//import com.arsahub.backend.repositories.PointHistoryRepository
//import com.arsahub.backend.repositories.UserRepository
//import jakarta.transaction.Transactional
//import org.springframework.stereotype.Component
//import java.time.Instant
//
//@Component
//class EventFinishScheduler(
//    private val eventRepository: EventRepository,
//    private val pointHistoryRepository: PointHistoryRepository,
//    private val userRepository: UserRepository
//) {
//
////    @Scheduled(fixedDelay = 5000000) // every 5 seconds
//    @Transactional
//    fun markFinishedEvents() {
//        val currentTime = Instant.now()
//        val finishedEvents = eventRepository.findUnfinishedEventsByEndTimeBefore(currentTime)
//
//        if (finishedEvents.isNotEmpty()) {
//            println("Finished ${finishedEvents.size} events (IDs: ${finishedEvents.map { it.activityId }})")
//        }
//
//        finishedEvents.map { event ->
//            event.markAsCompleted(currentTime)
//        }
//
//        val pointHistories: List<PointHistory> = finishedEvents.flatMap { event ->
//            println("event: ${event.activityId} has ${event.members.size} participations")
//            event.members.map { participation ->
//                participation.markAsCompleted(currentTime)
//                participation.user.addPoints(event.points)
//                PointHistory(
//                    user = participation.user,
//                    event = event,
//                    points = event.points,
//                    createdAt = currentTime,
//                    description = "Participated in event ${event.title} (ID: ${event.activityId})"
//                )
//            }
//        }
//
//        pointHistoryRepository.saveAll(pointHistories)
//        eventRepository.saveAll(finishedEvents)
//    }
//}

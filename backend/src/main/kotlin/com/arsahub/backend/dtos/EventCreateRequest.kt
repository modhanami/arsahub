//package com.arsahub.backend.dtos
//
//import com.arsahub.backend.models.Activity
//import java.time.Instant
//
//data class EventCreateRequest(
//    val title: String,
//    val description: String?,
//    val location: String?,
//    val startTime: Instant,
//    val endTime: Instant,
//    val organizerId: Long,
//    val points: Int
//) {
//    fun toEntity() = Activity(
//        title = title,
//        description = description,
//        organizerId = organizerId,
//        points = points,
//    )
//}
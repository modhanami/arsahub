package com.arsahub.backend.dtos

import java.time.LocalDateTime

data class EventUpdateRequest(
    val title: String?,
    val description: String?,
    val location: String?,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?,
)
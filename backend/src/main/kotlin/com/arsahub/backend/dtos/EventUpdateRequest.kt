package com.arsahub.backend.dtos

import java.time.Instant

data class EventUpdateRequest(
    val title: String?,
    val description: String?,
)
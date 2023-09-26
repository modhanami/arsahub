package com.arsahub.backend.dtos

import com.arsahub.backend.models.Organizer

data class OrganizerCreateRequest(
    val name: String
) {
    fun toEntity(): Organizer {
        return Organizer(
            name = name,
        )
    }
}

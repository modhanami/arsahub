package com.arsahub.backend.dtos

import com.arsahub.backend.models.Organizer

data class OrganizerResponse(
    val organizerId: Long,
    val name: String,
) {
    companion object {
        fun fromEntity(organizer: Organizer): OrganizerResponse {
            return OrganizerResponse(
                organizerId = organizer.organizerId ?: 0,
                name = organizer.name,
            )
        }
    }

}
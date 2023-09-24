package com.arsahub.backend.services

import com.arsahub.backend.models.Organizer

interface OrganizerService {
    fun createOrganizer(organizer: Organizer): Organizer
    fun updateOrganizer(organizerId: Long, updatedOrganizer: Organizer): Organizer
    fun getOrganizer(organizerId: Long): Organizer?
    fun listOrganizers(): List<Organizer>
    fun deleteOrganizer(organizerId: Long)
}

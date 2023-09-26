package com.arsahub.backend.services

import com.arsahub.backend.models.Organizer
import com.arsahub.backend.repositories.OrganizerRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service

interface OrganizerService {
    fun createOrganizer(organizer: Organizer): Organizer
    fun updateOrganizer(organizerId: Long, updatedOrganizer: Organizer): Organizer
    fun getOrganizer(organizerId: Long): Organizer?
    fun listOrganizers(): List<Organizer>
    fun deleteOrganizer(organizerId: Long)
}

@Service
class OrganizerServiceImpl(private val organizerRepository: OrganizerRepository) : OrganizerService {

    override fun createOrganizer(organizer: Organizer): Organizer {
        return organizerRepository.save(organizer)
    }

    override fun updateOrganizer(organizerId: Long, updatedOrganizer: Organizer): Organizer {
        val existingOrganizer = organizerRepository.findById(organizerId)
            .orElseThrow { EntityNotFoundException("Organizer with ID $organizerId not found") }

        existingOrganizer.name = updatedOrganizer.name

        return organizerRepository.save(existingOrganizer)
    }

    override fun getOrganizer(organizerId: Long): Organizer? {
        return organizerRepository.findById(organizerId).orElse(null)
    }

    override fun listOrganizers(): List<Organizer> {
        return organizerRepository.findAll()
    }

    override fun deleteOrganizer(organizerId: Long) {
        if (!organizerRepository.existsById(organizerId)) {
            throw EntityNotFoundException("Organizer with ID $organizerId not found")
        }
        organizerRepository.deleteById(organizerId)
    }
}
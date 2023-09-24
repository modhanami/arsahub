package com.arsahub.backend.models

import jakarta.persistence.*

@Entity
@Table(name = "organizer")
class Organizer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organizer_id")
    val organizerId: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String


)

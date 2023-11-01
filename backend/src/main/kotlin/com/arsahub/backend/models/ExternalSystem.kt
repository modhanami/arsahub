package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

@Entity
@Table(name = "external_system")
class ExternalSystem(
    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    var title: String? = null,

    @Size(max = 255)
    @Column(name = "description")
    var description: String? = null,

    @NotNull
    @Column(name = "api_key", nullable = false)
    var apiKey: UUID? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "external_system_id", nullable = false)
    var id: Long? = null
}
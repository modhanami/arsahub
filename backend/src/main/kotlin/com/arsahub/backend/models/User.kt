package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.util.*

@Entity
@Table(name = "user")
class User(

    @Column(name = "username", unique = true, nullable = false)
    val username: String,

    @Column(name = "name", nullable = false)
    val name: String,

    @NotNull
    @Column(name = "uuid", nullable = false)
    var uuid: UUID? = null
) : AuditedEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val userId: Long? = 0

}

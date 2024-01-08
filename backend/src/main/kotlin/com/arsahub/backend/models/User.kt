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
    var uuid: UUID? = null,

    @NotNull
    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    var password: String? = null,

    @NotNull
    @Column(name = "email", nullable = false, length = Integer.MAX_VALUE)
    var email: String? = null,
) : AuditedEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var userId: Long? = null

}

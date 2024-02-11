package com.arsahub.backend.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "user")
class User(
    @NotNull
    @Column(name = "external_user_id", nullable = false, length = Integer.MAX_VALUE)
    var externalUserId: String? = null,
    @NotNull
    @Column(name = "email", nullable = false, length = Integer.MAX_VALUE)
    var email: String? = null,
    @NotNull
    @Column(name = "first_name", nullable = false, length = Integer.MAX_VALUE)
    var firstName: String? = null,
    @NotNull
    @Column(name = "last_name", nullable = false, length = Integer.MAX_VALUE)
    var lastName: String? = null,
) : AuditedEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var userId: Long? = null
}

package com.arsahub.backend.models

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Entity
@Table(name = "role")
class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    val roleId: Long = 0,

    @Column(name = "name", nullable = false)
    val name: String
) {
    fun toAuthority(): GrantedAuthority {
        // Convert the role name to uppercase when creating authorities
        return SimpleGrantedAuthority("ROLE_${name.uppercase()}")
    }
}

enum class Roles {
    USER,
    ORGANIZER,
    ADMIN,
}

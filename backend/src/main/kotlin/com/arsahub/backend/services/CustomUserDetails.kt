package com.arsahub.backend.services

import com.arsahub.backend.models.Role
import com.arsahub.backend.models.Roles
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class CustomUserDetails(
    val userId: Long,
    username: String,
    authorities: Collection<GrantedAuthority>,
    val role: Role
) : User(username, "password", authorities) {
    fun isAdmin(): Boolean {
        return role.name.uppercase() == Roles.ADMIN.name
    }

    fun isOrganizer(): Boolean {
        return role.name.uppercase() == Roles.ORGANIZER.name
    }

}
package com.arsahub.backend.services

import com.arsahub.backend.dtos.LoginRequest
import com.arsahub.backend.dtos.LoginResponse
import com.arsahub.backend.models.Role
import com.arsahub.backend.models.Roles
import com.arsahub.backend.repositories.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

interface UserService {
    fun login(loginRequest: LoginRequest): LoginResponse
    fun getUser(jwt: Jwt): CustomUserDetails
    fun getCurrentUser(): CustomUserDetails?
}

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    override fun login(loginRequest: LoginRequest): LoginResponse {
//        return userRepository.findByUsername(loginRequest.username)
//            ?.let { LoginResponse(it.username, it.password) }
//            ?: throw Exception("User not found"
        return LoginResponse("token")
    }

    override fun getUser(jwt: Jwt): CustomUserDetails {
        return CustomUserDetails(1, "username", emptyList(), Role(1, Roles.USER.name))
    }

    override fun getCurrentUser(): CustomUserDetails? {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication != null && authentication.isAuthenticated && authentication.principal is CustomUserDetails) {
            authentication.principal as CustomUserDetails
        } else {
            null
        }
    }
}
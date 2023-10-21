package com.arsahub.backend.services

import com.arsahub.backend.dtos.LoginResponse
import com.arsahub.backend.dtos.RegisterRequest
import com.arsahub.backend.models.User
import com.arsahub.backend.repositories.UserRepository
import com.nimbusds.jwt.SignedJWT
import org.springframework.stereotype.Service

interface UserService {
    //    fun login(loginRequest: LoginRequest): LoginResponse
//    fun getUser(jwt: Jwt): CustomUserDetails
//    fun getUser(user: User): CustomUserDetails
//    fun getCurrentUser(): CustomUserDetails?
    fun register(registerRequest: RegisterRequest): LoginResponse
}

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {
//    override fun login(loginRequest: LoginRequest): LoginResponse {
////        return userRepository.findByUsername(loginRequest.username)
////            ?.let { LoginResponse(it.username, it.password) }
////            ?: throw Exception("User not found"
//        return LoginResponse("token")
//    }
//
//    override fun getUser(jwt: Jwt): CustomUserDetails {
//        return CustomUserDetails(1, "username", emptyList(), Role(1, Roles.USER.name))
//    }
//
//    override fun getUser(user: User): CustomUserDetails {
//        val fetchedUser = userRepository.findByUsername(user.username)
//            ?: throw Exception("User not found")
//        return CustomUserDetails(fetchedUser.userId, fetchedUser.username, listOf(fetchedUser.role.toAuthority()), fetchedUser.role)
//    }
//
//    override fun getCurrentUser(): CustomUserDetails? {
//        val authentication = SecurityContextHolder.getContext().authentication
//        return if (authentication != null && authentication.isAuthenticated && authentication.principal is CustomUserDetails) {
//            authentication.principal as CustomUserDetails
//        } else {
//            null
//        }
//    }

    override fun register(registerRequest: RegisterRequest): LoginResponse {
        // extract sub from JWT without any validation
        val idToken = registerRequest.idToken
        val untrustedJWT = SignedJWT.parse(idToken);
        val jwtClaimsSet = untrustedJWT.jwtClaimsSet
        val externalUserId = jwtClaimsSet.getStringClaim("sub")
        val username = jwtClaimsSet.getStringClaim("email")
        val name = jwtClaimsSet.getStringClaim("name")
        val user = userRepository.findByExternalUserId(externalUserId)
        return if (user != null) {
            LoginResponse.fromEntity(user)
        } else {
            val newUser = userRepository.save(
                User(
                    username = username,
                    name = name,
                    externalUserId = externalUserId,
                )
            )
            LoginResponse.fromEntity(newUser)
        }
    }
}
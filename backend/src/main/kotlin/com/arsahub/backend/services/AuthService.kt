package com.arsahub.backend.services

import com.arsahub.backend.models.User
import com.arsahub.backend.repositories.UserRepository
import com.arsahub.backend.security.auth.OryService
import com.arsahub.backend.security.auth.parseOryIdentity
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import sh.ory.model.Identity

@Service
class AuthService(private val userRepository: UserRepository, private val oryService: OryService) {
    private val logger = KotlinLogging.logger {}

    fun syncOry(request: HttpServletRequest): User {
        val session = oryService.getSession(request.cookies.toList())
        val externalUser = session.identity
        requireNotNull(externalUser) { "No identity found in session" }
        return syncOry(externalUser)
    }

    fun syncOry(identity: Identity): User {
        val externalUser = identity.parseOryIdentity()
        var user = userRepository.findByExternalUserId(externalUser.id)
        if (user == null) {
            logger.info { "User not found for identity ${externalUser.id}, auto creating new user" }
            user =
                userRepository.save(
                    User(
                        externalUserId = externalUser.id,
                        email = externalUser.email,
                        firstName = externalUser.firstName,
                        lastName = externalUser.lastName,
                    ),
                )
        } else {
            logger.info { "User ${user.userId} found for identity ${externalUser.id}, updating user data" }
            user.email = externalUser.email
            user.firstName = externalUser.firstName
            user.lastName = externalUser.lastName
            userRepository.save(user)
        }

        return user
    }
}

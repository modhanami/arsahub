package com.arsahub.backend.services

import com.arsahub.backend.dtos.request.UserLoginRequest
import com.arsahub.backend.dtos.request.UserSignupRequest
import com.arsahub.backend.models.App
import com.arsahub.backend.models.User
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.UserRepository
import com.arsahub.backend.security.auth.AuthProperties
import io.jsonwebtoken.Jwts
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val apiKeyService: APIKeyService,
    private val appRepository: AppRepository,
    private val passwordEncoder: Argon2PasswordEncoder,
    private val authProperties: AuthProperties
) {

    fun generateAccessToken(user: User): String {
        val tomorrow = Date.from(
            Instant.now().plus(
                Duration.ofDays(1)
            )
        )
        val token = Jwts.builder()
            .subject(user.email)
            .claim("id", user.userId)
            .claim("email", user.email)
            .expiration(tomorrow)
            .signWith(authProperties.secretKey)
            .compact()

        return token
    }

    fun createUser(request: UserSignupRequest): User {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("User with this email already exists")
        }

        val passwordHash = passwordEncoder.encode(request.password)

        val newUser = User(
            email = request.email,
            password = passwordHash,
            username = request.email,
            name = request.email,
            uuid = UUID.randomUUID()
        )
        val savedUser = userRepository.save(newUser)

        // bootstrap app
        createAppForUser(savedUser)

        return savedUser
    }

    fun authenticate(request: UserLoginRequest): User {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("User with this email does not exist")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Email or password is incorrect")
        }

        return user
    }

    private fun createAppForUser(user: User): App {
        val newApp = App(
            title = "My App",
            owner = user,
            apiKey = apiKeyService.generateAPIKey()
        )
        return appRepository.save(newApp)
    }

}
package com.arsahub.backend.services

import com.arsahub.backend.dtos.request.UserLoginRequest
import com.arsahub.backend.dtos.request.UserSignupRequest
import com.arsahub.backend.exceptions.UnauthorizedException
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
    private val authProperties: AuthProperties,
) {
    class UnauthorizedUserException : UnauthorizedException("Email or password is incorrect")

    class UnauthorizedRefreshTokenException : UnauthorizedException("Refresh token is invalid")

    fun generateAccessToken(user: User): String {
        val expirationTime = Date.from(Instant.now().plus(ACCESS_TOKEN_LIFETIME))
        return generateToken(user, expirationTime)
    }

    fun generateRefreshToken(user: User): String {
        val expirationTime = Date.from(Instant.now().plus(REFRESH_TOKEN_LIFETIME))
        return generateToken(user, expirationTime)
    }

    private fun generateToken(
        user: User,
        expirationTime: Date,
    ): String {
        return Jwts.builder()
            .subject(user.email)
            .claim("id", user.userId)
            .claim("email", user.email)
            .expiration(expirationTime)
            .signWith(authProperties.secretKey)
            .compact()
    }

    data class UserCreateResult(val user: User, val app: App)

    fun createUser(request: UserSignupRequest): UserCreateResult {
        require(!userRepository.existsByEmail(request.email)) {
            "User with this email already exists"
        }

        val passwordHash = passwordEncoder.encode(request.password)

        val newUser =
            User(
                email = request.email,
                password = passwordHash,
                username = request.email,
                name = request.email,
                uuid = UUID.randomUUID(),
            )
        val savedUser = userRepository.save(newUser)

        // bootstrap app
        val app = createAppForUser(savedUser)

        return UserCreateResult(savedUser, app)
    }

    fun authenticate(request: UserLoginRequest): User {
        val user =
            userRepository.findByEmail(request.email)
                ?: throw UnauthorizedUserException()

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw UnauthorizedUserException()
        }

        return user
    }

    private fun createAppForUser(user: User): App {
        val newApp =
            App(
                title = "My App",
                owner = user,
                apiKey = apiKeyService.generateAPIKey(),
            )
        return appRepository.save(newApp)
    }

    fun authenticateRefreshToken(refreshToken: String): User {
        val claims =
            Jwts.parser()
                .verifyWith(authProperties.secretKey)
                .build()
                .parseSignedClaims(refreshToken)
                .payload

        val userId = claims["id"]?.toString()?.toLong() ?: throw UnauthorizedRefreshTokenException()
        return userRepository.findById(userId).orElseThrow { UnauthorizedRefreshTokenException() }
    }

    companion object {
        //        val ACCESS_TOKEN_LIFETIME: Duration = Duration.ofSeconds(2)
        val ACCESS_TOKEN_LIFETIME: Duration = Duration.ofMinutes(15)
        val REFRESH_TOKEN_LIFETIME: Duration = Duration.ofDays(7)
    }
}

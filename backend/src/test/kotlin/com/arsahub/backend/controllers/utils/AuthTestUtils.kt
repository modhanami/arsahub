package com.arsahub.backend.controllers.utils

import com.arsahub.backend.models.App
import com.arsahub.backend.models.User
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.UserRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.serpro69.kfaker.faker
import io.jsonwebtoken.Jwts
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.crypto.spec.SecretKeySpec

data class AuthSetup(
    val user: User,
    val app: App,
)

object AuthTestUtils {
    private lateinit var authSetup: AuthSetup
    private lateinit var secretKey: String

    fun setGlobalAuthSetup(authSetup: AuthSetup) {
        this.authSetup = authSetup
    }

    fun setGlobalSecretKey(secretKey: String) {
        this.secretKey = secretKey
    }

    fun MockMvc.performWithAppAuth(
        requestBuilder: MockHttpServletRequestBuilder,
        app: App = authSetup.app,
    ): ResultActions {
        return this.perform(requestBuilder.header("X-API-Key", "${app.apiKey}"))
    }

    fun MockMvc.performWithUserAuth(
        requestBuilder: MockHttpServletRequestBuilder,
        user: User = authSetup.user,
    ): ResultActions {
        val exp = Instant.now().plus(1, ChronoUnit.HOURS).epochSecond
        val partialSupabasePayload =
            """
            {
                "aud": "authenticated",
                "exp": $exp,
                "iat": 123,
                "iss": "https://hbioyxpzykniktpsdmoy.supabase.co/auth/v1",
                "sub": "${user.externalUserId}",
                "email": "${user.email}",
                "app_metadata": {
                    "provider": "google",
                    "providers": [
                        "google"
                    ]
                },
                "user_metadata": {
                    "email": "${user.email}",
                    "email_verified": true,
                    "full_name": "${user.name}",
                    "iss": "https://accounts.google.com",
                    "name": "${user.name}",
                    "phone_verified": false,
                    "provider_id": "${user.googleUserId}",
                    "sub": "${user.googleUserId}"
                },
                "role": "authenticated",
                "session_id": "fc653ecb-ca6e-4047-a08b-a688682de268"
            }
            """.trimIndent()

        val secretKey = SecretKeySpec(secretKey.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")

        val objectMapper = ObjectMapper()
        val jsonPayloadAsMap =
            objectMapper.convertValue(
                objectMapper.readTree(partialSupabasePayload),
                object : TypeReference<MutableMap<String, Any>>() {},
            )
        val signedJWT =
            Jwts.builder()
                .claims(jsonPayloadAsMap)
                .signWith(secretKey)
                .compact()

        val bearerToken = "Bearer $signedJWT"

        return this.perform(requestBuilder.header("Authorization", bearerToken))
    }

    fun setupAuth(
        userRepository: UserRepository,
        appRepository: AppRepository,
    ): AuthSetup {
        val faker = faker { }

        val (currentUser, currentApp) =
            run {
                val user =
                    userRepository.save(
                        User(
                            externalUserId = faker.random.nextUUID(),
                            googleUserId = faker.random.nextUUID(),
                            email = faker.internet.email(),
                            name = faker.name.name(),
                        ),
                    )

                val app =
                    appRepository.save(
                        App(
                            title = faker.name.name(),
                            apiKey = faker.random.nextUUID(),
                            owner = user,
                        ),
                    )

                Pair(user, app)
            }

        return AuthSetup(
            user = currentUser,
            app = currentApp,
        )
    }
}

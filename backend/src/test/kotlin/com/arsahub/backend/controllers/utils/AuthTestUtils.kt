package com.arsahub.backend.controllers.utils

import com.arsahub.backend.models.App
import com.arsahub.backend.models.User
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.UserRepository
import com.arsahub.backend.services.APIKeyService
import io.mockk.every
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import java.util.*

data class AuthSetup(
    val user: User,
    val app: App
)

object AuthTestUtils {
    private var authSetup: AuthSetup? = null

    fun MockMvc.performWithAppAuth(
        requestBuilder: MockHttpServletRequestBuilder,
        app: App = authSetup?.app ?: throw IllegalStateException("Auth setup not initialized")
    ): ResultActions {
        return this.perform(requestBuilder.header("Authorization", "Bearer ${app.apiKey}"))
    }

    fun setupAuth(
        userRepository: UserRepository, // Spy
        appRepository: AppRepository, // Spy
        apiKeyService: APIKeyService // Mock
    ): AuthSetup {
        val currentUser = userRepository.save(
            User(
                username = "user",
                name = "User",
                uuid = UUID.fromString("00000000-0000-0000-0000-000000000001")
            )
        )
        val currentApp = appRepository.save(
            App(
                title = "app",
                description = "desc",
                apiKey = UUID.fromString("00000000-0000-0000-0000-000000000101").toString(),
                owner = currentUser
            )
        )

        val apiKey = currentApp.apiKey!!
        every { apiKeyService.validateKeyForApp(any<App>(), apiKey) } returns true

        authSetup = AuthSetup(
            user = currentUser,
            app = currentApp
        )
        return AuthSetup(
            user = currentUser,
            app = currentApp
        )
    }

}
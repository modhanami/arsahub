package com.arsahub.backend.controllers.utils

import com.arsahub.backend.dtos.request.UserSignupRequest
import com.arsahub.backend.models.App
import com.arsahub.backend.models.User
import com.arsahub.backend.services.AuthService
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

data class AuthSetup(
    val user: User,
    val app: App
)

object AuthTestUtils {
    private lateinit var authSetup: AuthSetup

    fun MockMvc.performWithAppAuth(
        requestBuilder: MockHttpServletRequestBuilder,
        app: App = authSetup.app
    ): ResultActions {
        return this.perform(requestBuilder.header("X-API-Key", "${app.apiKey}"))
    }

    fun setupAuth(
        authService: AuthService, // Spy
    ): AuthSetup {
        val (currentUser, currentApp) = authService.createUser(
            UserSignupRequest(
                email = "a@a.a",
                password = "password"
            )
        )

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
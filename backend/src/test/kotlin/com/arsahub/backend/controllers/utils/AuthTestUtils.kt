package com.arsahub.backend.controllers.utils

import com.arsahub.backend.dtos.request.UserSignupRequest
import com.arsahub.backend.models.App
import com.arsahub.backend.models.User
import com.arsahub.backend.services.AuthService
import io.github.serpro69.kfaker.faker
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

data class AuthSetup(
    val user: User,
    val app: App,
)

object AuthTestUtils {
    private lateinit var authSetup: AuthSetup

    fun setGlobalAuthSetup(authSetup: AuthSetup) {
        this.authSetup = authSetup
    }

    fun MockMvc.performWithAppAuth(
        requestBuilder: MockHttpServletRequestBuilder,
        app: App = authSetup.app,
    ): ResultActions {
        return this.perform(requestBuilder.header("X-API-Key", "${app.apiKey}"))
    }

    fun setupAuth(authService: AuthService): AuthSetup {
        val faker = faker { }

        val (currentUser, currentApp) =
            authService.createUser(
                UserSignupRequest(
                    email = faker.internet.email(),
                    password = "password",
                ),
            )

        return AuthSetup(
            user = currentUser,
            app = currentApp,
        )
    }
}

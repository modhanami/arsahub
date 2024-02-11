package com.arsahub.backend.controllers.utils

import com.arsahub.backend.models.App
import com.arsahub.backend.models.User
import com.arsahub.backend.repositories.AppRepository
import com.arsahub.backend.repositories.UserRepository
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
                            email = faker.internet.email(),
                            firstName = faker.name.firstName(),
                            lastName = faker.name.lastName(),
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

package com.arsahub.backend.security.auth

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationConverter
import sh.ory.ApiClient
import sh.ory.ApiException
import sh.ory.Configuration
import sh.ory.api.FrontendApi

class ExternalUserAuthenticationConverter : AuthenticationConverter {
    private val defaultClient: ApiClient = Configuration.getDefaultApiClient()

    private val logger = KotlinLogging.logger {}

    init {
        defaultClient.setBasePath("https://focused-sammet-ts4c30tm6u.projects.oryapis.com")
//        val oryAccessToken: HttpBearerAuth = defaultClient.getAuthentication("oryAccessToken") as HttpBearerAuth
//        oryAccessToken.bearerToken = ""
    }

    override fun convert(request: HttpServletRequest): Authentication {
        val apiInstance = FrontendApi(defaultClient)
        val orySessionCookies =
            request.cookies?.filter { it.name.startsWith("ory_session") }
                ?.joinToString(";") { "${it.name}=${it.value}" }

        requireNotNull(orySessionCookies) { "No ory_session cookie found" }

        try {
            val result = apiInstance.toSession(null, orySessionCookies, null)
            val identity = result.identity
            requireNotNull(identity) { "No identity found in session" }

            return ExternalUserAuthenticationToken.unauthenticated(
                orySessionCookies,
                identity,
            )
        } catch (e: ApiException) {
            logger.error(e) { "Error while converting external user authentication" }
            logger.error { "Status code: ${e.code}" }
            logger.error { "Reason: ${e.responseBody}" }
            logger.error { "Response headers: ${e.responseHeaders}" }
            throw e
        } catch (e: Exception) {
            logger.error(e) { "Error while converting external user authentication" }
            throw e
        }
    }
}

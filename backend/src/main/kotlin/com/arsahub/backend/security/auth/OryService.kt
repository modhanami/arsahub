package com.arsahub.backend.security.auth

import com.arsahub.backend.security.auth.OryUtils.filterCookies
import com.arsahub.backend.security.auth.OryUtils.joinCookiesAsString
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.Cookie
import org.springframework.stereotype.Service
import sh.ory.ApiClient
import sh.ory.ApiException
import sh.ory.api.FrontendApi
import sh.ory.model.Session

@Service
class OryService(
    apiClient: ApiClient,
) {
    private val logger = KotlinLogging.logger {}
    private val apiInstance = FrontendApi(apiClient)

    fun getSession(cookies: List<Cookie>): Session {
        val orySessionCookies = filterCookies(cookies.toList())
        logger.info { "Ory session cookies size: ${orySessionCookies.size}" }

        require(orySessionCookies.isNotEmpty()) { "No ory_session cookie found" }
        val orySessionCookiesString = joinCookiesAsString(orySessionCookies)
        try {
            val result = apiInstance.toSession(null, orySessionCookiesString, null)
            return result
        } catch (e: ApiException) {
            logger.error(e) { "Error while getting session from Ory" }
            logger.error { "Status code: ${e.code}" }
            logger.error { "Reason: ${e.responseBody}" }
            logger.error { "Response headers: ${e.responseHeaders}" }
            throw e
        } catch (e: Exception) {
            logger.error(e) { "Error while getting session from Ory" }
            throw e
        }
    }
}

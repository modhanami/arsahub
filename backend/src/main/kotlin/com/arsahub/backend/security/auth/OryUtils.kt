package com.arsahub.backend.security.auth

import jakarta.servlet.http.Cookie
import sh.ory.model.Identity

data class OryIdentity(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
)

fun Identity.parseOryIdentity(): OryIdentity {
    val traits = this.traits as? Map<*, *>
    val email = traits?.get("email") as? String ?: ""
    val firstName = traits?.get("first_name") as? String ?: ""
    val lastName = traits?.get("last_name") as? String ?: ""
    val fullName = "$firstName $lastName"

    return OryIdentity(
        id = this.id,
        email = email,
        firstName = firstName,
        lastName = lastName,
        fullName = fullName,
    )
}

object OryUtils {
    fun filterCookies(cookies: List<Cookie>): List<Cookie> {
        return cookies.filter { it.name.startsWith("ory_session") }
    }

    fun joinCookiesAsString(cookies: List<Cookie>): String {
        return cookies.joinToString(";") { "${it.name}=${it.value}" }
    }
}

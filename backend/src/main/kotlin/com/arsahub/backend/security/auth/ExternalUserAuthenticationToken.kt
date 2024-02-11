package com.arsahub.backend.security.auth

import com.arsahub.backend.models.User
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import sh.ory.model.Identity

data class UserIdentity(
    val externalUserId: String,
    val internalUserId: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
) {
    companion object {
        fun create(
            identity: Identity,
            internalUser: User,
        ): UserIdentity {
            val traits = identity.traits as? Map<*, *>
            val email = traits?.get("email") as? String ?: ""
            val firstName = traits?.get("first_name") as? String ?: ""
            val lastName = traits?.get("last_name") as? String ?: ""
            val fullName = "$firstName $lastName"

            val internalUserId = requireNotNull(internalUser.userId) { "Internal user ID is null" }

            return UserIdentity(
                externalUserId = identity.id,
                email = email,
                firstName = firstName,
                lastName = lastName,
                fullName = fullName,
                internalUserId = internalUserId,
            )
        }
    }
}

class ExternalUserAuthenticationToken(
    val sessionCookie: String,
    val identity: Identity,
    val userIdentity: UserIdentity? = null,
    val user: User? = null,
    private val authenticated: Boolean = false,
) : Authentication {
    override fun getName(): String {
        return userIdentity?.email ?: ""
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    override fun getCredentials(): Any {
        return sessionCookie
    }

    override fun getDetails(): Any? {
        return userIdentity
    }

    override fun getPrincipal(): Any? {
        return userIdentity
    }

    override fun isAuthenticated(): Boolean {
        return authenticated
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        throw UnsupportedOperationException()
    }

    companion object {
        fun authenticated(
            sessionCookie: String,
            identity: Identity,
            user: User,
        ): ExternalUserAuthenticationToken {
            return ExternalUserAuthenticationToken(
                sessionCookie,
                identity,
                UserIdentity.create(
                    identity,
                    user,
                ),
                user,
                true,
            )
        }

        fun unauthenticated(
            sessionCookie: String,
            identity: Identity,
        ): ExternalUserAuthenticationToken {
            return ExternalUserAuthenticationToken(sessionCookie, identity, null, null, false)
        }
    }
}

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
            identity.parseOryIdentity().let {
                return UserIdentity(
                    externalUserId = it.id,
                    internalUserId = internalUser.userId!!,
                    email = it.email,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    fullName = it.fullName,
                )
            }
        }
    }
}

class ExternalUserAuthenticationToken(
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
        return ""
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
            identity: Identity,
            user: User,
        ): ExternalUserAuthenticationToken {
            return ExternalUserAuthenticationToken(
                identity,
                UserIdentity.create(
                    identity,
                    user,
                ),
                user,
                true,
            )
        }

        fun unauthenticated(identity: Identity): ExternalUserAuthenticationToken {
            return ExternalUserAuthenticationToken(identity, null, null, false)
        }
    }
}

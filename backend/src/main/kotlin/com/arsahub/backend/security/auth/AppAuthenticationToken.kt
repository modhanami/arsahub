package com.arsahub.backend.security.auth

import com.arsahub.backend.models.App
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

data class AppAuthenticationToken(
    val apiKey: String,
    val app: App? = null,
    private val authenticated: Boolean = false,
) : Authentication {
    override fun getName(): String {
        return app?.title ?: ""
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    override fun getCredentials(): Any {
        return apiKey
    }

    override fun getDetails(): Any? {
        return app
    }

    override fun getPrincipal(): Any? {
        return app
    }

    override fun isAuthenticated(): Boolean {
        return authenticated
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        throw UnsupportedOperationException()
    }

    companion object {
        fun authenticated(
            apiKey: String,
            app: App
        ): AppAuthenticationToken {
            return AppAuthenticationToken(apiKey, app, true)
        }

        fun unauthenticated(
            appKey: String
        ): AppAuthenticationToken {
            return AppAuthenticationToken(appKey)
        }
    }
}
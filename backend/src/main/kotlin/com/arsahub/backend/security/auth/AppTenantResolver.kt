package com.arsahub.backend.security.auth

import com.arsahub.backend.models.App
import org.hibernate.cfg.AvailableSettings
import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder

@Component
class AppTenantResolver : CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer {
    override fun resolveCurrentTenantIdentifier(): String {
        try {
            val requestAttributes = RequestContextHolder.currentRequestAttributes()
            val app = requestAttributes.getAttribute(CURRENT_APP_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST)
            return if (app is App) {
                app.id.toString()
            } else {
                "0"
            }
        } catch (e: IllegalStateException) {
            return "0"
        }
    }

    override fun validateExistingCurrentSessions(): Boolean {
        return false
    }

    override fun customize(hibernateProperties: MutableMap<String?, Any?>) {
        hibernateProperties[AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER] = this
    }
}

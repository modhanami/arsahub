package com.arsahub.backend.security.auth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sh.ory.ApiClient

@Configuration
class OryConfig {
    @Bean
    fun defaultClient(): ApiClient {
        val client = sh.ory.Configuration.getDefaultApiClient()
        client.setBasePath("https://focused-sammet-ts4c30tm6u.projects.oryapis.com")
        return client
    }
}

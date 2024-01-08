package com.arsahub.backend.security.auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import javax.crypto.spec.SecretKeySpec

@Component
class AuthProperties {
    @Value("\${jwt.secret}")
    private lateinit var secret: String

    val secretKey: SecretKeySpec
        get() = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")

}
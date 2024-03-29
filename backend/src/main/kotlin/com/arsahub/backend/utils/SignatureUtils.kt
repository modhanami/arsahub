package com.arsahub.backend.utils

import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SignatureUtil {
    fun createSignature(
        secretKey: String,
        payload: String,
    ): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(), "HmacSHA256")
        mac.init(secretKeySpec)
        val hash = mac.doFinal(payload.toByteArray())
        return Base64.getEncoder().encodeToString(hash)
    }
}

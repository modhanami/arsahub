package com.arsahub.backend.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SignatureUtilsTest {
    @Test
    fun `SignatureUtils - createSignature() implements HMAC-SHA256 with base64 encoding`() {
        // Arrange
        val payload = "test payload"
        val secretKey = "key"

        // Act
        val signature1 = SignatureUtil.createSignature(secretKey, payload)
        val signature2 = SignatureUtil.createSignature(secretKey, payload)

        // Assert
        Assertions.assertEquals("OZs273u52OR+y0VJsduSq7itUXzlxn7yhIvr9TCjf44=", signature1)
        Assertions.assertEquals(signature1, signature2)
    }

}
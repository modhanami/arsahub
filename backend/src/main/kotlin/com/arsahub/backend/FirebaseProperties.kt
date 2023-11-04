package com.arsahub.backend

import org.springframework.boot.context.properties.ConfigurationProperties
import java.io.File

@ConfigurationProperties(prefix = "firebase")
data class FirebaseProperties(
    val serviceAccount: File? = null
)

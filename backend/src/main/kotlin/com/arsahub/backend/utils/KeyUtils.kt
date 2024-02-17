package com.arsahub.backend.utils

import java.util.regex.Pattern

object KeyUtils {
    fun generateKeyFromTitle(title: String): String {
        val regex = Pattern.compile("[a-zA-Z0-9_-]+")
        val matcher = regex.matcher(title)
        val matches = mutableListOf<String>()
        while (matcher.find()) {
            matches.add(matcher.group())
        }
        return matches.joinToString("_").lowercase()
    }
}

package com.arsahub.backend.security.auth

import org.springframework.security.core.annotation.AuthenticationPrincipal

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@AuthenticationPrincipal
annotation class CurrentApp

const val CURRENT_APP_ATTRIBUTE = "current_app"

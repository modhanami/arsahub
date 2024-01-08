package com.arsahub.backend.security.auth

import org.springframework.security.core.annotation.AuthenticationPrincipal

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@AuthenticationPrincipal
annotation class CurrentApp

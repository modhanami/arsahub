package com.arsahub.backend.controllers

import org.springframework.security.core.annotation.AuthenticationPrincipal
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy


@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@AuthenticationPrincipal(expression = "@userServiceImpl.getUser(#this)")
annotation class CurrentUser

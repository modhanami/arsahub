package com.arsahub.backend

import com.arsahub.backend.exceptions.ConflictException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Any> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to it.defaultMessage }
        val response = mapOf("message" to "Validation error", "errors" to errors)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflictExceptions(ex: ConflictException): ResponseEntity<Any> {
        val response = mapOf("message" to ex.message)
        return ResponseEntity(response, HttpStatus.CONFLICT)
    }
}
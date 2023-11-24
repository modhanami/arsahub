package com.arsahub.backend

import com.arsahub.backend.dtos.ApiError
import com.arsahub.backend.dtos.ApiValidationError
import com.arsahub.backend.exceptions.ConflictException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class CustomExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ApiValidationError> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Validation failed") }
        val response = ApiValidationError(errors)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflictExceptions(ex: ConflictException): ResponseEntity<ApiError> {
        val response = ApiError(ex.message ?: "Conflict")
        return ResponseEntity(response, HttpStatus.CONFLICT)
    }

}

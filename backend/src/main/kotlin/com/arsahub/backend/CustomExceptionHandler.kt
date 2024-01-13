package com.arsahub.backend

import com.arsahub.backend.dtos.response.ApiError
import com.arsahub.backend.dtos.response.ApiValidationError
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.exceptions.NotFoundException
import com.arsahub.backend.exceptions.UnauthorizedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ResponseStatusException

@ControllerAdvice
class CustomExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ApiValidationError> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Unknown error") }
        val response = ApiValidationError(errors)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentExceptions(ex: IllegalArgumentException): ResponseEntity<ApiError> {
        val response = ApiError(ex.message ?: "Illegal argument")
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflictExceptions(ex: ConflictException): ResponseEntity<ApiError> {
        val response = ApiError(ex.message ?: "Conflict")
        return ResponseEntity(response, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedExceptions(ex: UnauthorizedException): ResponseEntity<ApiError> {
        val response = ApiError(ex.message ?: "Unauthorized")
        return ResponseEntity(response, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundExceptions(ex: NotFoundException): ResponseEntity<ApiError> {
        val response = ApiError(ex.message ?: "Not found")
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusExceptions(ex: ResponseStatusException): ResponseEntity<ApiError> {
        val response = ApiError(ex.reason ?: "Unknown error")
        return ResponseEntity.status(ex.statusCode).body(response)
    }
}

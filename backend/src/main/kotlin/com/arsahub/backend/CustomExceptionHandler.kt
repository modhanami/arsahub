package com.arsahub.backend

import com.arsahub.backend.dtos.response.ApiError
import com.arsahub.backend.dtos.response.ApiValidationError
import com.arsahub.backend.exceptions.ConflictException
import com.arsahub.backend.exceptions.NotFoundException
import com.arsahub.backend.exceptions.UnauthorizedException
import io.jsonwebtoken.ClaimJwtException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class CustomExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ApiValidationError> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Unknown error") }
        val response = ApiValidationError(errors)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentExceptions(ex: IllegalArgumentException): ResponseEntity<ApiError> {
        val response = ApiError(ex.message ?: "Illegal argument")
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ConflictException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleConflictExceptions(ex: ConflictException): ResponseEntity<ApiError> {
        val response = ApiError(ex.message ?: "Conflict")
        return ResponseEntity(response, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(UnauthorizedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleUnauthorizedExceptions(ex: UnauthorizedException): ResponseEntity<ApiError> {
        val response = ApiError(ex.message ?: "Unauthorized")
        return ResponseEntity(response, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFoundExceptions(ex: NotFoundException): ResponseEntity<ApiError> {
        val response = ApiError(ex.message ?: "Not found")
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ClaimJwtException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleClaimJwtExceptions(ex: ClaimJwtException): ResponseEntity<ApiError> {
        val response = ApiError(ex.message ?: "Invalid token")
        return ResponseEntity(response, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusExceptions(ex: ResponseStatusException): ResponseEntity<ApiError> {
        val response = ApiError(ex.reason ?: "Unknown error")
        return ResponseEntity.status(ex.statusCode).body(response)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleDataIntegrityViolationExceptions(ex: DataIntegrityViolationException): ResponseEntity<ApiError> {
        val response = ApiError("Data integrity violation, please check your input is correct or not duplicated")
        return ResponseEntity(response, HttpStatus.CONFLICT)
    }
}

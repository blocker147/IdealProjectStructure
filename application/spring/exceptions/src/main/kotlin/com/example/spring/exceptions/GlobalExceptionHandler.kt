package com.example.spring.exceptions

import mu.KotlinLogging
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.Clock

private val log = KotlinLogging.logger {}

@Configuration
@ControllerAdvice
class GlobalExceptionHandler(
    private val clock: Clock,
) {
    @ExceptionHandler(Exception::class)
    fun handleInternalServerErrorException(exception: Exception): ResponseEntity<ErrorResponse> {
        log.error("Exception message: ${exception.message}", exception)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                clock.instant().toString(),
                exception.message ?: "Something went wrong"
            )
        )
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(exception: AuthenticationException): ResponseEntity<ErrorResponse> {
        log.warn("Authentication exception: ${exception.message}")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse(
                clock.instant().toString(),
                exception.message ?: "Something went wrong with authentication"
            )
        )
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAuthenticationException(exception: AccessDeniedException): ResponseEntity<ErrorResponse> {
        log.warn("Authorization exception: ${exception.message}")
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse(
                clock.instant().toString(),
                exception.message ?: "Something went wrong with authorization"
            )
        )
    }
}

data class ErrorResponse(val timestamp: String, val errorMessage: String)
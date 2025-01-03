package com.example.spring.exceptions

import mu.KotlinLogging
import org.springframework.context.annotation.Configuration
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
        return ResponseEntity.badRequest().body(
            ErrorResponse(
                clock.instant().toString(),
                exception.message ?: "Exception message not specified"
            )
        )
    }

    @ExceptionHandler(AuthenticationException::class, AccessDeniedException::class)
    fun handleAuthenticationException(exception: RuntimeException): ResponseEntity<ErrorResponse> {
        log.warn("Authentication exception message: ${exception.message}")
        return ResponseEntity.badRequest().body(
            ErrorResponse(
                clock.instant().toString(),
                exception.message ?: "Authentication exception message not specified"
            )
        )
    }
}

data class ErrorResponse(val timestamp: String, val errorMessage: String)
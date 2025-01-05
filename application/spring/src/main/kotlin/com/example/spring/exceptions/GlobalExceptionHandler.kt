package com.example.spring.exceptions

import com.example.domain.exceptions.ApplicationException
import com.example.infrastructure.client.productnutrition.exceptions.DownstreamException
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
    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(exception: ApplicationException): ResponseEntity<ErrorResponse> {
        logException(exception)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                clock.instant().toString(),
                exception.message ?: "Something went wrong"
            )
        )
    }

    @ExceptionHandler(DownstreamException::class)
    fun handleDownstreamException(exception: DownstreamException): ResponseEntity<ErrorResponse> {
        logException(exception)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                clock.instant().toString(),
                exception.message ?: "Something went wrong with downstream service"
            )
        )
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(exception: AuthenticationException): ResponseEntity<ErrorResponse> {
        logException(exception)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse(
                clock.instant().toString(),
                exception.message ?: "Something went wrong with authentication"
            )
        )
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAuthenticationException(exception: AccessDeniedException): ResponseEntity<ErrorResponse> {
        logException(exception)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse(
                clock.instant().toString(),
                exception.message ?: "Something went wrong with authorization"
            )
        )
    }

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

    private fun logException(exception: Exception) {
        log.warn("${exception.message}.${
            if(exception.stackTrace.isNotEmpty()) " Thrown from: ${exception.stackTrace[0]}" else ""
        }")
    }
}

data class ErrorResponse(val timestamp: String, val errorMessage: String)
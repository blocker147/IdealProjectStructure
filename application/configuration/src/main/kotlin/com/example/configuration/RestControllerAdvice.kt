package com.example.configuration

import mu.KotlinLogging
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

private val log = KotlinLogging.logger {}

@Configuration
@ControllerAdvice
class RestControllerAdvice {

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ErrorResponse {
        log.warn("Exception message: ${exception.message}", exception)
        return ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            System.currentTimeMillis(),
            exception.message ?: "Unknown"
        )
    }
}

data class ErrorResponse(val code: Int, val timestamp: Long, val errorMessage: String)
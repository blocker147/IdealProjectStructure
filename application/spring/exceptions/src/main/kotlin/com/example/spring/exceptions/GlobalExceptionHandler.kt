package com.example.spring.exceptions

import mu.KotlinLogging
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
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
    fun handleInternalServerErrorException(exception: Exception): ResponseEntity<InternalServerErrorResponse> {
        log.error("Exception message: ${exception.message}", exception)
        return ResponseEntity.badRequest().body(
            InternalServerErrorResponse(
                clock.instant().toString(),
                exception.message ?: "Exception message not specified"
            )
        )
    }
}
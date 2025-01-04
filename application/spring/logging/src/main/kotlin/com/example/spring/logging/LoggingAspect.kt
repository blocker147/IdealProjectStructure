package com.example.spring.logging

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import org.slf4j.MDC
import java.util.*

private val log = KotlinLogging.logger {}

//@Aspect
//@Component
class LoggingAspect(
    private val request: HttpServletRequest,
    private val response: HttpServletResponse
) {

    // Pointcut expression to match methods in classes annotated with @Controller or @RestController
    @Pointcut("execution(public * *(..)) && @within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller)")
    fun controllerMethods() {}

    @Around("controllerMethods()")
    fun logRequestAndResponse(pjp: ProceedingJoinPoint): Any? {
        val traceId = UUID.randomUUID().toString()
        MDC.put(MDCConstants.TRACE_ID.key, traceId)
        response.addHeader(MDCConstants.TRACE_ID.key, traceId)
        log.info { "-> ${request.method} ${request.requestURI}" }

        try {
            return pjp.proceed()
        } finally {
            log.info { "<- ${request.method} [${response.status}] ${request.requestURI}" }
            MDC.clear()
        }
    }
}
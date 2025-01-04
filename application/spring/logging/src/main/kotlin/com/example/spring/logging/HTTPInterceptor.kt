package com.example.spring.logging

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Configuration
class HTTPInterceptor : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(object : HandlerInterceptor {
            override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
                val traceId = UUID.randomUUID().toString()
                MDC.put(MDCConstants.TRACE_ID.key, traceId)
                logger.info { "-> ${request.method} ${request.requestURI}" }
                return true
            }

            override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
                logger.info { "<- ${request.method} [${response.status}] ${request.requestURI}" }
                MDC.clear()
            }
        })
    }
}
package com.example.spring.logging

import com.example.spring.utils.HTTPUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.UUID

private val log = KotlinLogging.logger {}

@Configuration
class HTTPInterceptor : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(CustomerInterceptor())
    }

    private class CustomerInterceptor : HandlerInterceptor {
        override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
            val path = request.requestURI
            if (HTTPUtils.isStaticResource(path)) return true
            val traceId = UUID.randomUUID().toString()
            MDC.put(MDCConstants.TRACE_ID.key, traceId)
            response.addHeader(MDCConstants.TRACE_ID.key, traceId)
            log.info { "-> ${request.method} $path" }
            return true
        }

        override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
            if (HTTPUtils.isStaticResource(request.requestURI)) {
                MDC.clear()
                return
            }
            log.info { "<- ${request.method} [${response.status}] ${request.requestURI}" }
            MDC.clear()
        }
    }
}
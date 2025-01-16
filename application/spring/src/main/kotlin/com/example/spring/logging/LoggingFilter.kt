package com.example.spring.logging

import com.example.spring.utils.HTTPUtils
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

private val log = KotlinLogging.logger {}

@Component
class LoggingFilter : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val path = request.requestURI
        if (HTTPUtils.isStaticResource(path)) {
            filterChain.doFilter(request, response)
            return
        }

        val traceId = UUID.randomUUID().toString()
        MDC.put(MDCConstants.TRACE_ID.key, traceId)
        response.addHeader(MDCConstants.TRACE_ID.key, traceId)
        log.info { "-> ${request.method} $path" }

        filterChain.doFilter(request, response)

        if (HTTPUtils.isStaticResource(request.requestURI)) {
            MDC.clear()
            return
        }
        log.info { "<- ${request.method} [${response.status}] ${request.requestURI}" }
        MDC.clear()
    }
}
package com.example.spring.security.config

import com.example.spring.security.utils.HTTPUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExceptionResolver

@Component
class AuthenticationExceptionHandler(
    @Qualifier("handlerExceptionResolver") @Autowired val resolver: HandlerExceptionResolver
) : AuthenticationEntryPoint, AccessDeniedHandler {

    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
        exceptionHandler(request, response, authException)
    }

    override fun handle(request: HttpServletRequest, response: HttpServletResponse, accessDeniedException: AccessDeniedException) {
        exceptionHandler(request, response, accessDeniedException)
    }

    private fun exceptionHandler(request: HttpServletRequest, response: HttpServletResponse, exception: Exception) {
        val isRequestFromBrowser = HTTPUtils.isRequestFromBrowser(request)

        if (isRequestFromBrowser) {
            HTTPUtils.redirect(response, "/")
        } else {
            resolver.resolveException(request, response, null, exception)
        }
    }
}
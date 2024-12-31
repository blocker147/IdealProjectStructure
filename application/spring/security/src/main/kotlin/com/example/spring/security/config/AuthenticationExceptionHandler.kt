package com.example.spring.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class AuthenticationExceptionHandler : AuthenticationEntryPoint, AccessDeniedHandler {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val isRequestFromBrowser = isRequestFromBrowser(
            request.getHeader(HttpHeaders.USER_AGENT),
            request.getHeader(HttpHeaders.ACCEPT)
        )

        if (isRequestFromBrowser) {
            redirectBrowserClientToAuthenticationPage(response)
        } else {
            authenticationErrorResponse(response)
        }
    }

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        val isRequestFromBrowser = isRequestFromBrowser(
            request.getHeader(HttpHeaders.USER_AGENT),
            request.getHeader(HttpHeaders.ACCEPT)
        )

        if (isRequestFromBrowser) {
            redirectBrowserClientToAuthenticationPage(response)
        } else {
            accessDeniedErrorResponse(response)
        }
    }

    private fun isRequestFromBrowser(userAgent: String?, accept: String?): Boolean {
        if (userAgent == null) return false
        if (accept != "text/html") return false

        val browserKeywords = listOf(
            "Chrome",   // Google Chrome
            "Safari",   // Safari (note: Chrome includes "Safari" too)
            "Firefox",  // Mozilla Firefox
            "Edg",      // Microsoft Edge
            "OPR",      // Opera (new versions)
            "Opera",    // Opera (older versions)
            "MSIE",     // Internet Explorer
            "Trident"   // Internet Explorer (newer versions)
        )

        // Check if the User-Agent contains any browser keyword
        return browserKeywords.any { keyword ->
            userAgent.contains(keyword, ignoreCase = true)
        }
    }

    private fun redirectBrowserClientToAuthenticationPage(response: HttpServletResponse) {
        response.status = HttpServletResponse.SC_FOUND
        response.setHeader(HttpHeaders.LOCATION, "/")
    }

    private fun authenticationErrorResponse(response: HttpServletResponse) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"
        val errorResponse = mapOf(
            "error" to "Unauthorized",
            "message" to "You need to provide a valid JWT token to access this resource",
            "timestamp" to System.currentTimeMillis()
        )
        ObjectMapper().writeValue(response.outputStream, errorResponse)
    }

    private fun accessDeniedErrorResponse(response: HttpServletResponse) {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "application/json"
        val errorResponse = mapOf(
            "error" to "Access Denied",
            "message" to "Pum purum pum pum. Where is your JWT?",
            "timestamp" to System.currentTimeMillis()
        )
        ObjectMapper().writeValue(response.outputStream, errorResponse)
    }
}
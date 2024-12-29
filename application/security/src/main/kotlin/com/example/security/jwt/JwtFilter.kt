package com.example.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val authenticationManager: JwtAuthenticationProvider,
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        // todo: limit URL to filter
        if (path == "/") return true
        return super.shouldNotFilter(request)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            checkJwtInHeader(request, filterChain, response)
            checkJwtInCookie(request, filterChain, response)
        } catch (e: Exception) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            response.writer.write("Something went wrong during JWT check")
            return
        }
    }

    private fun checkJwtInHeader(
        request: HttpServletRequest,
        filterChain: FilterChain,
        response: HttpServletResponse
    ) {
        request.getHeader("Authorization")?.let {
            if (it.startsWith("Bearer ")) {
                val token = it.substring(7)

                if (JwtUtil.validateToken(token)) {
                    authenticateUser(token, filterChain, request, response)
                } else {
                    response.status = HttpServletResponse.SC_UNAUTHORIZED
                    response.writer.write("Unauthorized: JWT token from Header is invalid")
                    return
                }
            }
        }
    }

    private fun checkJwtInCookie(
        request: HttpServletRequest,
        filterChain: FilterChain,
        response: HttpServletResponse
    ) {
        val cookies = request.cookies
        if (cookies != null && cookies.isNotEmpty() && cookies.map { it.name }.contains("jwt-token")) {
            val token = cookies.first { it.name == "jwt-token" }.value

            if (JwtUtil.validateToken(token)) {
                authenticateUser(token, filterChain, request, response)
            } else {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("Unauthorized: JWT token from Cookies is invalid")
                return
            }

        } else {
            doFilter(request, response, filterChain)
        }
    }

    private fun authenticateUser(
        token: String,
        filterChain: FilterChain,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val authRequest = JwtAuthenticationToken(token)
        val authResult = authenticationManager.authenticate(authRequest)
        SecurityContextHolder.getContext().authentication = authResult
        response.addHeader("Authorization", "Bearer $token")
        filterChain.doFilter(request, response)
    }
}

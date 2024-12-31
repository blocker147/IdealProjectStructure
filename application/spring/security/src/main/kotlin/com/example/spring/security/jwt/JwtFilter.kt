package com.example.spring.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val authenticationManager: JwtAuthenticationProvider,
    private val jwtUtil: JwtUtil,
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
            if (checkJwtInHeader(request, response)) {
                filterChain.doFilter(request, response)
                return
            }

            if (checkJwtInCookie(request, response)) {
                filterChain.doFilter(request, response)
                return
            }

            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
//            response.writer.write("JWT token not provided neither in Header nor in Cookies")
            filterChain.doFilter(request, response)
        }
    }

    private fun checkJwtInHeader(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Boolean {
        request.getHeader("Authorization")?.let {
            if (it.startsWith("Bearer ")) {
                val token = it.substring(7)

                if (jwtUtil.validateToken(token)) {
                    authenticateUser(token)
                    return true
                } else {
                    response.status = HttpServletResponse.SC_UNAUTHORIZED
                    response.writer.write("Unauthorized: JWT token from Header is invalid")
                    return true
                }
            }
        }
        return false
    }

    private fun checkJwtInCookie(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Boolean {
        val cookies = request.cookies
        if (cookies != null && cookies.isNotEmpty() && cookies.map { it.name }.contains("jwt-token")) {
            val token = cookies.first { it.name == "jwt-token" }.value

            if (jwtUtil.validateToken(token)) {
                authenticateUser(token)
                return true
            } else {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("Unauthorized: JWT token from Cookies is invalid")
                return true
            }

        }
        return false
    }

    private fun authenticateUser(token: String) {
        val authRequest = JwtAuthenticationToken(token)
        val authResult = authenticationManager.authenticate(authRequest)
        SecurityContextHolder.getContext().authentication = authResult
    }
}

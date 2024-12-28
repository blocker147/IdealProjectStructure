package com.example.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.security.core.AuthenticationException

@Component
class JwtFilter(
    private val authenticationManager: JwtAuthenticationProvider,
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        if (path == "/auth-with-github") return true
        if (path == "/login/oauth2/code/github") return true
        return super.shouldNotFilter(request)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)

            // todo: if token is null or invalid redirect to
            //  /auth-with-github in case of automatic OAuth2 need to think how

            val authRequest = JwtAuthenticationToken(token)

            try {
                val authResult = authenticationManager.authenticate(authRequest)
                SecurityContextHolder.getContext().authentication = authResult
            } catch (ex: AuthenticationException) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("Unauthorized: ${ex.message}")
                return
            }
        }

//        val authorizationHeader = request.getHeader("Authorization")
//
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            val token = authorizationHeader.substring(7)
//            try {
//                val claims: Claims = JwtUtil.validateToken(token)
//                val username = claims.subject
//
//                if (username != null) {
//                    val authentication = JwtAuthenticationToken(username)
//                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
//                    SecurityContextHolder.getContext().authentication = authentication
//                }
//            } catch (e: Exception) {
//                response.status = HttpServletResponse.SC_UNAUTHORIZED
//                return
//            }
//        }
        filterChain.doFilter(request, response)
    }
}

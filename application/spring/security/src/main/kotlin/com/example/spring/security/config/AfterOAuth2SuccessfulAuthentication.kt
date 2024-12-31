package com.example.spring.security.config

import com.example.spring.security.jwt.JwtUtil
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.RedirectStrategy
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class AfterOAuth2SuccessfulAuthentication(
    private val jwtUtil: JwtUtil
) : AuthenticationSuccessHandler {

    private val redirectStrategy: RedirectStrategy = DefaultRedirectStrategy()

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val username = authentication.name
        val token = jwtUtil.generateToken(username)

        response.addCookie(Cookie("jwt-token", token).apply {
            isHttpOnly = true
            secure = true
            path = "/"
        })
        response.addHeader("Authorization", "Bearer $token")

        redirectStrategy.sendRedirect(request, response, "/home")
    }
}
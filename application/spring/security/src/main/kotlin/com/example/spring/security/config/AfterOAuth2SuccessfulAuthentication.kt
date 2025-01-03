package com.example.spring.security.config

import com.example.spring.security.jwt.JWTService
import com.example.spring.security.jwt.JWTType
import com.example.spring.security.utils.HTTPUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class AfterOAuth2SuccessfulAuthentication(
    private val jwtService: JWTService
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val username = authentication.name

        val accessToken = jwtService.generateJWT(username, JWTType.ACCESS_TOKEN)
        val refreshToken = jwtService.generateJWT(username, JWTType.REFRESH_TOKEN)

        HTTPUtils.addJWTCookie(response, JWTType.ACCESS_TOKEN, accessToken)
        HTTPUtils.addJWTCookie(response, JWTType.REFRESH_TOKEN, refreshToken)

        // fixme: perhaps Location header can be used with status of 3xx
        DefaultRedirectStrategy().sendRedirect(request, response, "/home")
    }
}
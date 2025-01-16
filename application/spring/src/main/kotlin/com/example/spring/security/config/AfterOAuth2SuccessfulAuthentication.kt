package com.example.spring.security.config

import com.example.spring.security.jwt.JWTService
import com.example.spring.security.jwt.JWTType.ACCESS_TOKEN
import com.example.spring.security.jwt.JWTType.REFRESH_TOKEN
import com.example.spring.utils.HTTPUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

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

        log.info { "User $username has successfully authenticated with OAuth2" }

        val accessToken = jwtService.generateJWT(username, ACCESS_TOKEN)
        val refreshToken = jwtService.generateJWT(username, REFRESH_TOKEN)

        HTTPUtils.addJWTCookie(response, ACCESS_TOKEN, accessToken)
        HTTPUtils.addJWTCookie(response, REFRESH_TOKEN, refreshToken)

        HTTPUtils.redirect(response, "/home")
    }
}
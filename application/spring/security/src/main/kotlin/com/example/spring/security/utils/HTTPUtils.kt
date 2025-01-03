package com.example.spring.security.utils

import com.example.spring.security.jwt.JWTType
import com.example.spring.security.jwt.JWTType.ACCESS_TOKEN
import com.example.spring.security.jwt.JWTType.REFRESH_TOKEN
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders

object HTTPUtils {
    /**
     * If request is from browser then JWT token is added to the cookie, otherwise it is added to the header
     * */
    fun addJWTResponse(request: HttpServletRequest, response: HttpServletResponse, type: JWTType, token: String) {
        if (isRequestFromBrowser(request)) {
            addJWTCookie(response, type, token)
        } else {
            addJWTHeader(response, type, token)
        }
    }

    fun addJWTCookie(response: HttpServletResponse, type: JWTType, token: String) {
        response.addCookie(Cookie(type.cookieName, token).apply {
            maxAge = type.expiration
            isHttpOnly = true
            secure = true
            path = "/"
        })
    }

    private fun addJWTHeader(response: HttpServletResponse, type: JWTType, token: String) {
        response.setHeader(HttpHeaders.AUTHORIZATION, "${type.authPrefix}$token")
    }

    fun clearJWTCookies(response: HttpServletResponse, vararg types: JWTType = arrayOf(ACCESS_TOKEN, REFRESH_TOKEN)) {
        types.forEach {
            response.addCookie(Cookie(it.cookieName, "").apply {
                maxAge = 0
                secure = true
                path = "/"
            })
        }
    }

    /**
     * Attempts to determine if the request is from a browser
     * @return true if the request is from a browser
     * */
    fun isRequestFromBrowser(request: HttpServletRequest): Boolean {
        val userAgent = request.getHeader(HttpHeaders.USER_AGENT) ?: return false
        val accept = request.getHeader(HttpHeaders.ACCEPT) ?: return false

        if (!accept.contains("text/html")) return false

        return listOf("Chrome", "Safari", "Firefox", "Mozilla", "Edg", "OPR", "Opera", "MSIE", "Trident").any {
            userAgent.contains(it, ignoreCase = true)
        }
    }

    /**
     * Attempts to read JWT from the Authorization header or from the cookie
     * @return JWT token or null if not found
     * */
    fun getJWT(request: HttpServletRequest, type: JWTType): String? = when (type) {
        ACCESS_TOKEN -> getJWTFromHeader(request, type.authPrefix) ?: getJWTFromCookie(request, type.cookieName)
        REFRESH_TOKEN -> getJWTFromHeader(request, type.authPrefix) ?: getJWTFromCookie(request, type.cookieName)
    }

    private fun getJWTFromHeader(request: HttpServletRequest, prefix: String): String? {
        return request.getHeader(HttpHeaders.AUTHORIZATION)?.let {
            if (it.startsWith(prefix)) {
                it.substring(prefix.length)
            } else null
        }
    }

    private fun getJWTFromCookie(request: HttpServletRequest, cookieName: String): String? {
        return request.cookies
            ?.firstOrNull { it.name == cookieName }
            ?.value
    }

    /**
     * Redirect user to the specified location
     * @param location URL to redirect to (e.g. /home)
     * */
    fun redirect(response: HttpServletResponse, location: String) {
        response.status = HttpServletResponse.SC_FOUND
        response.setHeader(HttpHeaders.LOCATION, location)
    }
}
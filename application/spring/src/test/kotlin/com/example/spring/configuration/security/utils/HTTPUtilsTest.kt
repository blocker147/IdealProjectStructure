package com.example.spring.configuration.security.utils

import com.example.spring.security.jwt.JWTType.ACCESS_TOKEN
import com.example.spring.security.jwt.JWTType.REFRESH_TOKEN
import com.example.spring.utils.HTTPUtils
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockCookie
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class HTTPUtilsTest {
    private val request: MockHttpServletRequest = MockHttpServletRequest()
    private val response: MockHttpServletResponse = MockHttpServletResponse()
    private val token = "j.w.t"

    private val target = HTTPUtils

    @Test
    fun `when request come from browser - then return true`() {
        request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
        request.addHeader(HttpHeaders.ACCEPT, "text/html")

        val actual = target.isRequestFromBrowser(request)

        actual shouldBe true
    }

    @Test
    fun `when request come from non-browser - then return false`() {
        val actual = target.isRequestFromBrowser(request)

        actual shouldBe false
    }

    @Test
    fun `when request come from browser - then add JWT in cookie`() {
        request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
        request.addHeader(HttpHeaders.ACCEPT, "text/html")
        val type = ACCESS_TOKEN

        target.addJWTResponse(request, response, type, token)

        val actual = response.cookies[0]
        actual.name shouldBe type.cookieName
        actual.value shouldBe token
    }

    @Test
    fun `when request come from non-browser - then add JWT in header`() {
        val type = ACCESS_TOKEN

        target.addJWTResponse(request, response, type, token)

        val actual = response.getHeader(HttpHeaders.AUTHORIZATION)
        actual shouldBe "${type.authPrefix}$token"
    }

    @Test
    fun `when cookie is cleared - then return empty cookie`() {
        request.setCookies(MockCookie(ACCESS_TOKEN.cookieName, token), MockCookie(REFRESH_TOKEN.cookieName, token))

        target.clearJWTCookies(response)

        response.cookies.size shouldBe 2
        response.cookies.forEach {
            it.value shouldBe ""
            it.maxAge shouldBe 0
            it.secure shouldBe true
            it.path shouldBe "/"
        }
    }

    @Test
    fun `when access token is in cookie - then return it from cookie`() {
        request.setCookies(MockCookie(ACCESS_TOKEN.cookieName, token))

        val actual = target.getJWT(request, ACCESS_TOKEN)

        actual shouldBe token
    }

    @Test
    fun `when access token is in header - then return it from header`() {
        request.addHeader(HttpHeaders.AUTHORIZATION, "${ACCESS_TOKEN.authPrefix}$token")

        val actual = target.getJWT(request, ACCESS_TOKEN)

        actual shouldBe token
    }

    @Test
    fun `when refresh token is in cookie - then return it from cookie`() {
        request.setCookies(MockCookie(REFRESH_TOKEN.cookieName, token))

        val actual = target.getJWT(request, REFRESH_TOKEN)

        actual shouldBe token
    }

    @Test
    fun `when refresh token is in header - then return it from header`() {
        request.addHeader(HttpHeaders.AUTHORIZATION, "${REFRESH_TOKEN.authPrefix}$token")

        val actual = target.getJWT(request, REFRESH_TOKEN)

        actual shouldBe token
    }

    @Test
    fun `when access token is not present - then return null`() {
        val actual = target.getJWT(request, ACCESS_TOKEN)

        actual shouldBe null
    }

    @Test
    fun `when refresh token is not present - then return null`() {
        val actual = target.getJWT(request, REFRESH_TOKEN)

        actual shouldBe null
    }

    @Test
    fun `when redirect - then return 302 status code with location header`() {
        val location = "/redirect"

        target.redirect(response, location)

        response.status shouldBe 302
        response.getHeader(HttpHeaders.LOCATION) shouldBe location
    }
}
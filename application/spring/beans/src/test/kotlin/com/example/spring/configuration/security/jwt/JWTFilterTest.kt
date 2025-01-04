package com.example.spring.configuration.security.jwt

import com.example.spring.configuration.security.jwt.JWTType.ACCESS_TOKEN
import com.example.spring.configuration.security.jwt.JWTType.REFRESH_TOKEN
import io.kotest.matchers.shouldBe
import io.mockk.*
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockCookie
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView

class JWTFilterTest {
    private val jwtServiceMock: JWTService = mockk()
    private val resolver: HandlerExceptionResolver = mockk()
    private val request: MockHttpServletRequest = MockHttpServletRequest()
    private val response: MockHttpServletResponse = MockHttpServletResponse()
    private val filterChain: MockFilterChain = MockFilterChain()
    private lateinit var target: JWTFilter

    private val username = "username"
    private val token = "j.w.t"

    @BeforeEach
    fun setUp() {
        target = JWTFilter(jwtServiceMock, resolver)
        every { jwtServiceMock.generateJWT(username, any()) } returns token
        every { jwtServiceMock.verifyJWT(any(), any()) } returns username
        every { jwtServiceMock.blackListJWT(any()) } returns Unit
    }

    @Test
    fun `when request has only access token in cookie - then authenticate user`() {
        val type = ACCESS_TOKEN
        request.setCookies(MockCookie(type.cookieName, token))

        target.doFilter(request, response, filterChain)

        verify { jwtServiceMock.verifyJWT(token, type) }
        SecurityContextHolder.getContext().authentication.name shouldBe username
    }

    @Test
    fun `when request has only access token in header - then authenticate user`() {
        val type = ACCESS_TOKEN
        request.addHeader(HttpHeaders.AUTHORIZATION, "${type.authPrefix}$token")

        target.doFilter(request, response, filterChain)

        verify { jwtServiceMock.verifyJWT(token, type) }
        SecurityContextHolder.getContext().authentication.name shouldBe username
    }

    @Test
    fun `when request come from browser and has only refresh token in cookie - then authenticate user and provide access token in cookie`() {
        val type = REFRESH_TOKEN
        request.addHeader(HttpHeaders.ACCEPT, "text/html")
        request.addHeader(HttpHeaders.USER_AGENT, "Chrome")
        request.setCookies(MockCookie(type.cookieName, token))

        target.doFilter(request, response, filterChain)

        verify { jwtServiceMock.verifyJWT(token, type) }
        SecurityContextHolder.getContext().authentication.name shouldBe username
    }

    @Test
    fun `when request come from non-browsers and has only refresh token in header - then authenticate user and provide access token in header`() {
        val type = REFRESH_TOKEN
        request.addHeader(HttpHeaders.AUTHORIZATION, "${type.authPrefix}$token")

        target.doFilter(request, response, filterChain)

        verify { jwtServiceMock.verifyJWT(token, type) }
        SecurityContextHolder.getContext().authentication.name shouldBe username
    }

    @Test
    fun `when request come from browser and doesn't have any tokens - then clear cookies and redirect`() {
        every { jwtServiceMock.verifyJWT(any(), any()) } returns null
        request.setCookies(MockCookie(ACCESS_TOKEN.cookieName, token), MockCookie(REFRESH_TOKEN.cookieName, token))
        request.addHeader(HttpHeaders.ACCEPT, "text/html")
        request.addHeader(HttpHeaders.USER_AGENT, "Chrome")

        target.doFilter(request, response, filterChain)

        response.cookies.size shouldBe 2
        response.cookies.forEach {
            it.value shouldBe ""
            it.maxAge shouldBe 0
            it.secure shouldBe true
            it.path shouldBe "/"
        }
        response.status shouldBe HttpServletResponse.SC_FOUND
        response.getHeader(HttpHeaders.LOCATION) shouldBe "/"
    }

    @Test
    fun `when request come from non-browser and doesn't have any tokens - then resolver receives exception`() {
        every { resolver.resolveException(any(), any(), any(), any()) } returns ModelAndView()

        target.doFilter(request, response, filterChain)

        verify { resolver.resolveException(request, response, null, any()) }
        response.status shouldBe HttpServletResponse.SC_UNAUTHORIZED
    }
}
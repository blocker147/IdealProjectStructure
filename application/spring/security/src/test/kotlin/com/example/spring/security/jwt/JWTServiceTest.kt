package com.example.spring.security.jwt

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

class JWTServiceTest {
    private val jwtSigningKey = "ZXcXQZHDcogtSJV1oZhG4On5xn4rmCcLp3Qp5nwFKh79kRYg38St7yvSyBCfdaMrIeNWK5bwHmblRehRGAvg9nTXIVL7W0AHQhlrheV3EGpzZiaifnifIKeyr8ZyvCqjzVzUFnZMb79uIlHp82DoMUR867cVuHRv7qlpXdPcVDrB0dRQ1IIuQYhIJLRQ3IFkmfgInIITczZRsTceHu0OhzTSmicLL9CSRPfZvpEsPO4Zta5Ewf2dot3Dh5WrHawF"
    private val instant = Instant.parse("1997-01-01T00:00:00Z")
    private val clockFake = Clock.fixed(instant, Clock.systemUTC().zone)
    private lateinit var target: JWTService

    @BeforeEach
    fun setup() {
        target = JWTService(jwtSigningKey, clockFake)
    }

    private val username = "username"

    @Test
    fun `when generate access token - then return valid token`() {
        val (token, expiresAfter) = target.generateToken(username, JWTService.TokenType.ACCESS_TOKEN)

        val actual = target.verifyToken(token, JWTService.TokenType.ACCESS_TOKEN)

        actual.shouldNotBeNull()
        actual shouldBe username
        expiresAfter shouldBe JWTService.JWT_ACCESS_TOKEN_EXPIRATION
    }

    @Test
    fun `when generate refresh token - then return valid token`() {
        val (token, expiresAfter) = target.generateToken(username, JWTService.TokenType.REFRESH_TOKEN)

        val actual = target.verifyToken(token, JWTService.TokenType.REFRESH_TOKEN)

        actual.shouldNotBeNull()
        actual shouldBe username
        expiresAfter shouldBe JWTService.JWT_REFRESH_TOKEN_EXPIRATION
    }

    @Test
    fun `when access token is valid - then return username`() {
        val (token, _) = target.generateToken(username, JWTService.TokenType.ACCESS_TOKEN)

        val actual = target.verifyToken(token, JWTService.TokenType.ACCESS_TOKEN)

        actual.shouldNotBeNull()
        actual shouldBe username
    }

    @Test
    fun `when refresh token is valid - then return username`() {
        val (token, _) = target.generateToken(username, JWTService.TokenType.REFRESH_TOKEN)

        val actual = target.verifyToken(token, JWTService.TokenType.REFRESH_TOKEN)

        actual.shouldNotBeNull()
        actual shouldBe username
    }

    @Test
    fun `when access token is invalid - then return null`() {
        val (token, _) = target.generateToken(username, JWTService.TokenType.ACCESS_TOKEN)

        val actual = target.verifyToken(token.reversed(), JWTService.TokenType.ACCESS_TOKEN)

        actual.shouldBeNull()
    }

    @Test
    fun `when refresh token is invalid - then return null`() {
        val (token, _) = target.generateToken(username, JWTService.TokenType.REFRESH_TOKEN)

        val actual = target.verifyToken(token.reversed(), JWTService.TokenType.REFRESH_TOKEN)

        actual.shouldBeNull()
    }

    @Test
    fun `when refresh token is added in blacklist but requesting for access token - then return username`() {
        val (token, _) = target.generateToken(username, JWTService.TokenType.REFRESH_TOKEN)
        target.blacklistRefreshToken(token)

        val actual = target.verifyToken(token, JWTService.TokenType.ACCESS_TOKEN)

        actual.shouldNotBeNull()
        actual shouldBe username
    }

    @Test
    fun `when refresh token is added in blacklist and requesting for refresh token - then return null`() {
        val (token, _) = target.generateToken(username, JWTService.TokenType.REFRESH_TOKEN)
        target.blacklistRefreshToken(token)

        val actual = target.verifyToken(token, JWTService.TokenType.REFRESH_TOKEN)

        actual.shouldBeNull()
    }

    @Test
    fun `when requesting expired access token - then return null`() {
        val (token, _) = target.generateToken(username, JWTService.TokenType.ACCESS_TOKEN)
        target.clock = Clock.fixed(instant
            .plus(JWTService.JWT_ACCESS_TOKEN_EXPIRATION.toLong(), ChronoUnit.MINUTES)
            .plus(1, ChronoUnit.SECONDS),
            Clock.systemUTC().zone
        )

        val actual = target.verifyToken(token, JWTService.TokenType.ACCESS_TOKEN)

        actual.shouldBeNull()
    }

    @Test
    fun `when requesting expired refresh token - then return null`() {
        val (token, _) = target.generateToken(username, JWTService.TokenType.REFRESH_TOKEN)
        target.clock = Clock.fixed(instant
            .plus(JWTService.JWT_REFRESH_TOKEN_EXPIRATION.toLong(), ChronoUnit.MINUTES)
            .plus(1, ChronoUnit.SECONDS),
            Clock.systemUTC().zone
        )

        val actual = target.verifyToken(token, JWTService.TokenType.REFRESH_TOKEN)

        actual.shouldBeNull()
    }
}
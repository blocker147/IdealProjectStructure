package com.example.spring.security.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.impl.FixedClock
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

class JWTServiceTest {
    private val jwtSigningKey = "ZXcXQZHDcogtSJV1oZhG4On5xn4rmCcLp3Qp5nwFKh79kRYg38St7yvSyBCfdaMrIeNWK5bwHmblRehRGAvg9nTXIVL7W0AHQhlrheV3EGpzZiaifnifIKeyr8ZyvCqjzVzUFnZMb79uIlHp82DoMUR867cVuHRv7qlpXdPcVDrB0dRQ1IIuQYhIJLRQ3IFkmfgInIITczZRsTceHu0OhzTSmicLL9CSRPfZvpEsPO4Zta5Ewf2dot3Dh5WrHawF"
    private val instant = Instant.parse("1997-01-01T00:00:00Z")
    private val clockFake = Clock.fixed(instant, Clock.systemUTC().zone)
    private val target = JWTService(jwtSigningKey, clockFake)
    private val username = "username"
    private val secretKey = Keys.hmacShaKeyFor(jwtSigningKey.toByteArray())
    private val jsonWebTokenClock: io.jsonwebtoken.Clock = FixedClock(clockFake.millis())

    @Test
    fun `when generate access token - then return valid token`() {
        val (token, _) = target.generateToken(username, JWTService.TokenType.ACCESS_TOKEN)

        val (sub, iat, exp) = getPayload(token)

        sub.shouldNotBeNull()
        iat.shouldNotBeNull()
        exp.shouldNotBeNull()
        exp shouldBe clockFake.millis() + JWTService.JWT_ACCESS_TOKEN_EXPIRATION
    }

    @Test
    fun `when generate refresh token - then return valid token`() {
        val (token, _) = target.generateToken(username, JWTService.TokenType.REFRESH_TOKEN)

        val (sub, iat, exp) = getPayload(token)

        sub.shouldNotBeNull()
        iat.shouldNotBeNull()
        exp.shouldNotBeNull()
        exp shouldBe clockFake.millis() + JWTService.JWT_REFRESH_TOKEN_EXPIRATION
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

    private fun getPayload(token: String): Payload {
        val claims = Jwts.parser()
            .clock(jsonWebTokenClock)
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
        return Payload(
            sub = claims.subject,
            iat = claims.issuedAt.time,
            exp = claims.expiration.time,
        )
    }

    private data class Payload(
        val sub: String,
        val iat: Long,
        val exp: Long,
    )
}
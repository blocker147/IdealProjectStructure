package com.example.spring.security.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.FixedClock
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Clock
import java.util.Date

@Component
class JWTService(
    @Value("\${jwt.signing-secret-key}") private val jwtSigningKey: String,
    var clock: Clock,
) {
    enum class TokenType { ACCESS_TOKEN, REFRESH_TOKEN }

    companion object {
        private const val ONE_MINUTE = 1000 * 60

        const val JWT_ACCESS_TOKEN_AUTHORIZATION_PREFIX = "Bearer "
        const val JWT_ACCESS_TOKEN_COOKIE_NAME = "jwt_access_token"
        const val JWT_ACCESS_TOKEN_EXPIRATION = ONE_MINUTE * 5

        const val JWT_REFRESH_TOKEN_AUTHORIZATION_PREFIX = "Bearer-Refresh "
        const val JWT_REFRESH_TOKEN_COOKIE_NAME = "jwt_refresh_token"
        const val JWT_REFRESH_TOKEN_EXPIRATION = ONE_MINUTE * 30
    }

    private val secretKey = Keys.hmacShaKeyFor(jwtSigningKey.toByteArray())
    private val blackList = mutableSetOf<String>()

    /**
    * @return token and it's expiration time in seconds
    * */
    fun generateToken(username: String, tokenType: TokenType): Pair<String, Int> {
        val now = clock.millis()
        val expiresAfter = when (tokenType) {
            TokenType.ACCESS_TOKEN -> JWT_ACCESS_TOKEN_EXPIRATION
            TokenType.REFRESH_TOKEN -> JWT_REFRESH_TOKEN_EXPIRATION
        }
        val jwtBuilder = Jwts.builder()
            .subject(username)
            .issuedAt(Date(now))
            .signWith(secretKey)
            .expiration(Date(now + expiresAfter))

        return Pair(jwtBuilder.compact(), expiresAfter)
    }

    /**
     * @return username
     * */
    fun verifyToken(token: String, tokenType: TokenType): String? {
        if (tokenType == TokenType.REFRESH_TOKEN && blackList.contains(token)) return null
        return try {
            Jwts.parser()
                .clock(FixedClock(clock.millis()))
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
                .subject
        } catch (e: Exception) {
            null
        }
    }

    fun blacklistRefreshToken(token: String) = blackList.add(token)
}
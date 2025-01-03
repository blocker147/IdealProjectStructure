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
    private val secretKey = Keys.hmacShaKeyFor(jwtSigningKey.toByteArray())
    private val blackList = mutableSetOf<String>()

    /**
    * @return token and it's expiration time in seconds
    * */
    fun generateJWT(username: String, type: JWTType): String {
        val now = clock.millis()
        val jwtBuilder = Jwts.builder()
            .subject(username)
            .issuedAt(Date(now))
            .signWith(secretKey)
            .expiration(Date(now + type.expiration))

        return jwtBuilder.compact()
    }

    /**
     * @return username from token if it's valid, otherwise null
     * */
    fun verifyJWT(token: String, type: JWTType): String? {
        if (type == JWTType.REFRESH_TOKEN && blackList.contains(token)) return null
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

    fun blackListJWT(token: String) {
        blackList.add(token)
    }
}
package com.example.spring.configuration.security.jwt

import com.example.spring.configuration.security.jwt.JWTType.REFRESH_TOKEN
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.FixedClock
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Clock
import java.util.Date
import java.util.UUID

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
            .expiration(Date(now + type.expirationInMinutes))

        if (type == REFRESH_TOKEN) {
            val jti = UUID.randomUUID().toString()
            jwtBuilder.id(jti)
        }

        return jwtBuilder.compact()
    }

    /**
     * @return username from token if it's valid, otherwise null
     * */
    fun verifyJWT(token: String, type: JWTType): String? {
        if (type == REFRESH_TOKEN && blackList.contains(token)) return null
        return try {
            val payload = Jwts.parser()
                .clock(FixedClock(clock.millis()))
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload

            if (type == REFRESH_TOKEN && payload.id == null) null
            else payload.subject
        } catch (e: Exception) {
            null
        }
    }

    fun blackListJWT(token: String) {
        blackList.add(token)
    }
}
package com.example.spring.security.jwt

import com.example.spring.security.jwt.JWTType.REFRESH_TOKEN
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.FixedClock
import io.jsonwebtoken.security.Keys
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Clock
import java.util.Date
import java.util.UUID

private val log = KotlinLogging.logger {}

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

        log.debug { "Generating new ${type.name}" }
        return jwtBuilder.compact()
    }

    /**
     * @return username from token if it's valid, otherwise null
     * */
    fun verifyJWT(token: String, type: JWTType): String? {
        return try {
            if (type == REFRESH_TOKEN && blackList.contains(token)) {
                throw IllegalStateException("Attempting to access blacklisted token")
            }

            val payload = Jwts.parser()
                .clock(FixedClock(clock.millis()))
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload

            if (type == REFRESH_TOKEN && payload.id == null) {
                throw IllegalStateException("Token is missing jti")
            }
            else payload.subject
        } catch (e: Exception) {
            log.debug { "${type.name}: ${e.message}" }
            null
        }
    }

    fun blackListJWT(token: String) {
        blackList.add(token)
        log.debug { "Blacklisting ${REFRESH_TOKEN.name}" }
    }
}
package com.example.spring.security.jwt

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil(
    @Value("\${jwt.signing-secret-key}") private val jwtSigningKey: String
) {
    private val secretKey = Keys.hmacShaKeyFor(jwtSigningKey.toByteArray())

    fun generateToken(username: String): String {
        val claims: Map<String, Any> = mapOf(
            "username" to username,
//            "roles" to listOf("ROLE_USER", "ROLE_ADMIN")
        )
        return Jwts.builder()
            .claims(claims)
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
            .signWith(secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        try {
            val claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
            val expiration = claims.payload.expiration
            return !expiration.before(Date())
        } catch (e: UnsupportedJwtException) {
            // UnsupportedJwtException – if the jwt argument does not represent a signed Claims JWT

        } catch (e: JwtException) {
            //JwtException – if the jwt string cannot be parsed or validated as required.

        } catch (e: IllegalArgumentException) {
            //IllegalArgumentException – if the jwt string is null or empty or only whitespace

        }
        return true
    }

    fun getUsernameFromToken(token: String): String {
        val claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
        return claims.subject
    }
}
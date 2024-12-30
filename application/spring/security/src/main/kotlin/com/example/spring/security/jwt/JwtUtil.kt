package com.example.spring.security.jwt

import io.jsonwebtoken.Jwts
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
        val claims: Map<String, Any> = mapOf("username" to username)
        return Jwts.builder()
            .claims(claims)
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        val claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
        val expiration = claims.payload.expiration
        return !expiration.before(Date())
    }

    fun getUsernameFromToken(token: String): String {
        val claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
        return claims.subject
    }

    fun getRolesFromToken(token: String): List<String> {
        val claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
        return claims["roles"] as List<String>
    }
}

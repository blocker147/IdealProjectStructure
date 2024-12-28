package com.example.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class AuthController {
    private val secretKey = Keys
        .hmacShaKeyFor("UWpXQZHDcogtSJV1oZhG4On5xn4rmCcLp3Qp5nwFKh79kRYg38St7yvSyBCfdaMrIeNWK5bwHmblRehRGAvg9nTXIVL7W0AHQhlrheV3EGpzZiaifnifIKeyr8ZyvCqjzVzUFnZMb79uIlHp82DoMUR867cVuHRv7qlpXdPcVDrB0dRQ1IIuQYhIJLRQ3IFkmfgInIITczZRsTceHu0OhzTSmicLL9CSRPfZvpEsPO4Zta5Ewf2dot3Dh5WrHawF".toByteArray())

    @PostMapping("/generate-token")
    fun generateToken(authentication: Authentication): Map<String, String> {
        val expirationTime = 60 * 60 * 1000
        val now = System.currentTimeMillis()

        val token = Jwts.builder()
            .subject(authentication.name)
            .claim("roles", authentication.authorities.map { it.authority })
            .issuedAt(Date(now))
            .expiration(Date(now + expirationTime))
            .signWith(secretKey)
            .compact()

        return mapOf("token" to token)
    }

    @GetMapping("/jwt-secured")
    fun getSecuredData(): Map<String, String> {
        return mapOf("message" to "This is a secured message! You have a valid JWT token.")
    }
}

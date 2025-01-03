package com.example.spring.security.jwt

private const val ONE_MINUTE = 1000 * 60

enum class JWTType(
    val cookieName: String,
    val expirationInMinutes: Int,
    val authPrefix: String,
) {
    ACCESS_TOKEN("jwt_access_token", ONE_MINUTE * 5,"Bearer "),
    REFRESH_TOKEN("jwt_refresh_token", ONE_MINUTE * 30, "Bearer-Refresh ");

    fun expirationInSeconds() = expirationInMinutes / 1000
}
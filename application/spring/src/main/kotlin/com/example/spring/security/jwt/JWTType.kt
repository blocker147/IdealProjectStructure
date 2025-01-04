package com.example.spring.security.jwt

private const val ONE_MINUTE = 1000 * 60
private const val FIFTEEN_MINUTES = ONE_MINUTE * 15
private const val ONE_DAY = ONE_MINUTE * 60 * 24
enum class JWTType(
    val cookieName: String,
    val expirationInMinutes: Int,
    val authPrefix: String,
) {
    ACCESS_TOKEN("jwt_access_token", FIFTEEN_MINUTES,"Bearer "),
    REFRESH_TOKEN("jwt_refresh_token", ONE_DAY, "Bearer-Refresh ");

    fun expirationInSeconds() = expirationInMinutes / 1000
}
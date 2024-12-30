package com.example.spring.security.jwt

import org.springframework.security.authentication.AbstractAuthenticationToken

class JwtAuthenticationToken(private val token: String) : AbstractAuthenticationToken(null) {
    override fun getCredentials(): Any = token
    override fun getPrincipal(): Any? = null
}

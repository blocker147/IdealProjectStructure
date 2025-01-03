package com.example.spring.security.config

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

object AuthenticationUtils {
    fun authenticate(username: String, roles: List<String>) {
        val authorities = roles.map { SimpleGrantedAuthority(it) }
        val authentication = UsernamePasswordAuthenticationToken(username, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication
    }
}
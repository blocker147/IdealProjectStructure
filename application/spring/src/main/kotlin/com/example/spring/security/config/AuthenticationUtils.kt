package com.example.spring.security.config

import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

private val log = KotlinLogging.logger {}

object AuthenticationUtils {
    fun authenticate(username: String, roles: List<String>) {
        log.debug { "Authenticating user $username with roles: $roles" }
        val authorities = roles.map { SimpleGrantedAuthority(it) }
        val authentication = UsernamePasswordAuthenticationToken(username, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication
    }
}
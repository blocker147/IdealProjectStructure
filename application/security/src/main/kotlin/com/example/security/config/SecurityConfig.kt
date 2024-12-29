package com.example.security.config

import com.example.security.jwt.JwtFilter
import com.example.security.jwt.JwtUtil
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.RedirectStrategy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtFilter: JwtFilter,
    private val successHandler: CustomAuthenticationSuccessHandler
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { it
                .requestMatchers("/", "/favicon.ico", "/static/**", "/public/**").permitAll()
                .anyRequest().authenticated()
            }
            .oauth2Login { it
                .loginPage("/")
                .successHandler(successHandler)
            }
                // # map to custom authorities after login via OAuth2
                // https://docs.spring.io/spring-security/site/docs/5.0.x/reference/html/oauth2login-advanced.html#oauth2login-advanced-map-authorities
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}

@Component
class CustomAuthenticationSuccessHandler : AuthenticationSuccessHandler {
    private val redirectStrategy: RedirectStrategy = DefaultRedirectStrategy()

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val username = authentication.name
        val token = JwtUtil.generateToken(username)

        response.addCookie(Cookie("jwt-token", token).apply {
            isHttpOnly = true
            secure = true
            path = "/"
        })
        response.addHeader("Authorization", "Bearer $token")

        redirectStrategy.sendRedirect(request, response, "/home")
    }
}
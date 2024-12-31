package com.example.spring.security.config

import com.example.spring.security.jwt.JwtFilter
import com.example.spring.security.jwt.JwtUtil
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.RedirectStrategy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtFilter: JwtFilter,
    private val successHandler: AuthenticationSuccessHandler,
    private val securityViolationHandler: SecurityViolationHandler,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { it
                .requestMatchers("/", "/favicon.ico", "/static/**", "/public/**", "/error").permitAll()
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
            .exceptionHandling { it
                .authenticationEntryPoint(securityViolationHandler)
                .accessDeniedHandler(securityViolationHandler)
            }

        return http.build()
    }
}

@Component
class SecurityViolationHandler : AuthenticationEntryPoint, AccessDeniedHandler {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val isBrowser = isBrowserRequest(
            request.getHeader(HttpHeaders.USER_AGENT),
            request.getHeader(HttpHeaders.ACCEPT)
        )

        if (isBrowser) {

            // Redirect for browsers
            response.status = HttpServletResponse.SC_FOUND
            response.setHeader(HttpHeaders.LOCATION, "/")
        } else {

            // Return meaningful JSON response for others
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            val errorResponse = mapOf(
                "error" to "Unauthorized",
                "message" to "You need to provide a valid JWT token to access this resource",
                "timestamp" to System.currentTimeMillis()
            )
            ObjectMapper().writeValue(response.outputStream, errorResponse)
        }
    }

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        val isBrowser = isBrowserRequest(
            request.getHeader(HttpHeaders.USER_AGENT),
            request.getHeader(HttpHeaders.ACCEPT)
        )

        if (isBrowser) {

            // Redirect for browsers
            response.status = HttpServletResponse.SC_FOUND
            response.setHeader(HttpHeaders.LOCATION, "/")
        } else {

            // Return meaningful JSON response for others
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.contentType = "application/json"
            val errorResponse = mapOf(
                "error" to "Access Denied",
                "message" to "Pum purum pum pum. Where is JWT?",
                "timestamp" to System.currentTimeMillis()
            )
            ObjectMapper().writeValue(response.outputStream, errorResponse)
        }
    }

    private fun isBrowserRequest(userAgent: String?, accept: String?): Boolean {
        if (userAgent == null) return false
        if (accept != "text/html") return false

        val browserKeywords = listOf(
            "Chrome",   // Google Chrome
            "Safari",   // Safari (note: Chrome includes "Safari" too)
            "Firefox",  // Mozilla Firefox
            "Edg",      // Microsoft Edge
            "OPR",      // Opera (new versions)
            "Opera",    // Opera (older versions)
            "MSIE",     // Internet Explorer
            "Trident"   // Internet Explorer (newer versions)
        )

        // Check if the User-Agent contains any browser keyword
        return browserKeywords.any { keyword ->
            userAgent.contains(keyword, ignoreCase = true)
        }
    }
}

@Component
class OAuth2SuccessHandler(private val jwtUtil: JwtUtil) : AuthenticationSuccessHandler {
    private val redirectStrategy: RedirectStrategy = DefaultRedirectStrategy()

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val username = authentication.name
        val token = jwtUtil.generateToken(username)

        response.addCookie(Cookie("jwt-token", token).apply {
            isHttpOnly = true
            secure = true
            path = "/"
        })
        response.addHeader("Authorization", "Bearer $token")

        redirectStrategy.sendRedirect(request, response, "/home")
    }
}
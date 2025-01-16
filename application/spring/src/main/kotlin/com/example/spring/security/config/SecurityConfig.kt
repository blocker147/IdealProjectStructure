package com.example.spring.security.config

import com.example.spring.logging.LoggingFilter
import com.example.spring.security.jwt.JWTFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.context.SecurityContextHolderFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val loggingFilter: LoggingFilter,
    private val jwtFilter: JWTFilter,
    private val authenticationSuccessHandler: AuthenticationSuccessHandler,
    private val authenticationFailureHandler: AuthenticationFailureHandler,
    private val authenticationEntryPoint: AuthenticationEntryPoint,
    private val accessDeniedHandler: AccessDeniedHandler,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf {
                it.disable()
            }
            .authorizeHttpRequests { it
                .requestMatchers("/", "/favicon.ico", "/static/**", "/public/**", "/error").permitAll()
                .requestMatchers("/swagger-ui/**", "/custom-api-docs/**").permitAll()
                .anyRequest().authenticated()
            }
            .oauth2Login { it
                .loginPage("/")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(loggingFilter, SecurityContextHolderFilter::class.java)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { it
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            }

        return http.build()
    }
}
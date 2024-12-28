package com.example.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenFilter: JwtTokenFilter,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { it
                .requestMatchers("/").permitAll()
                .anyRequest().authenticated()
            }
            .oauth2Login { it
//                .successHandler(customAuthenticationSuccessHandler)
                .defaultSuccessUrl("/home", true)
            }
//            .addFilterBefore(oauth2AuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
//            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

        return http.build()
    }
}

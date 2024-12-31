package com.example.spring.security.config

import com.example.spring.security.jwt.JwtAuthenticationProvider
import com.example.spring.security.jwt.JwtFilter
import com.example.spring.security.jwt.JwtUtil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockCookie
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootTest(classes =
[
    SecurityConfig2Test.AppConfig::class,
    SecurityConfig::class,
    JwtFilter::class,
    JwtUtil::class,
    JwtAuthenticationProvider::class,
    AfterOAuth2SuccessfulAuthentication::class,
    AuthenticationExceptionHandler::class,
])
@AutoConfigureMockMvc
class SecurityConfig2Test {
    @Configuration
    class AppConfig {
        @Bean
        fun clientRegistrationRepository(): ClientRegistrationRepository {
            val clientRegistration = ClientRegistration.withRegistrationId("mock")
                .clientId("mock-client-id")
                .clientSecret("mock-client-secret")
                .authorizationUri("https://mock-authorization-uri")
                .tokenUri("https://mock-token-uri")
                .redirectUri("https://mock-redirect-uri")
                .scope("read", "write")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientName("Mock Client")
                .build()

            return InMemoryClientRegistrationRepository(clientRegistration)
        }

        @RestController
        class TestController {
            @GetMapping("/")
            fun publicEndpoint() = "Public endpoint"

            @GetMapping("/secured")
            fun securedEndpoint(authentication: Authentication) = "Secured endpoint"
        }
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jwtUtil: JwtUtil

    private val securedEndpoint = "/secured"

    @Test
    fun `when user tries to access public endpoint - then return 2xx`() {
        mockMvc.perform(get("/"))
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `when user has token in header - then return 2xx`() {
        val token = jwtUtil.generateToken("user")

        mockMvc.perform(
            get(securedEndpoint)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token"))
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `when user has token in cookie - then return 2xx`() {
        val token = jwtUtil.generateToken("user")
        val jwtCookie = MockCookie("jwt-token", token)

        mockMvc.perform(
            get(securedEndpoint)
            .cookie(jwtCookie))
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `when Browser user is not authenticated and has no JWT token - then redirect`() {
        mockMvc.perform(
            get(securedEndpoint)
                .header(HttpHeaders.USER_AGENT, "Firefox")
                .header(HttpHeaders.ACCEPT, "text/html")
        )
            .andExpect(status().is3xxRedirection)
    }

    @Test
    fun `when Non-Browser user is not authenticated and has no JWT token - then return 4xx`() {
        mockMvc.perform(get(securedEndpoint))
            .andExpect(status().is4xxClientError)
    }
}
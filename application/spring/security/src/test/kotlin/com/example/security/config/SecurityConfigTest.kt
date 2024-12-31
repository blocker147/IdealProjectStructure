package com.example.security.config

import com.example.spring.security.jwt.JwtAuthenticationProvider
import com.example.spring.security.jwt.JwtFilter
import com.example.spring.security.jwt.JwtUtil
import com.example.spring.security.config.OAuth2SuccessHandler
import com.example.spring.security.config.SecurityConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockCookie
import org.springframework.mock.web.MockServletContext
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

@Disabled
class SecurityConfigTest {
    private lateinit var context: WebApplicationContext
    private lateinit var mockMvc: MockMvc
    private val jwtUtil = AppConfig().jwtUtil()

    @Configuration
    private class AppConfig {
        @Bean
        fun jwtUtil() = JwtUtil("ZXcXQZHDcogtSJV1oZhG4On5xn4rmCcLp3Qp5nwFKh79kRYg38St7yvSyBCfdaMrIeNWK5bwHmblRehRGAvg9nTXIVL7W0AHQhlrheV3EGpzZiaifnifIKeyr8ZyvCqjzVzUFnZMb79uIlHp82DoMUR867cVuHRv7qlpXdPcVDrB0dRQ1IIuQYhIJLRQ3IFkmfgInIITczZRsTceHu0OhzTSmicLL9CSRPfZvpEsPO4Zta5Ewf2dot3Dh5WrHawF")

        @Bean
        fun jwtAuthenticationProvider(
            jwtUtil: JwtUtil
        ) = JwtAuthenticationProvider(jwtUtil)

        @Bean
        fun jwtFilter(
            jwtUtil: JwtUtil,
            jwtAuthenticationProvider: JwtAuthenticationProvider
        ) = JwtFilter(jwtAuthenticationProvider, jwtUtil)

        @Bean
        fun authenticationSuccessHandler(
            jwtUtil: JwtUtil
        ) = OAuth2SuccessHandler(jwtUtil)

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

    @BeforeEach
    fun setup() {
        context = AnnotationConfigWebApplicationContext().apply {
            servletContext = MockServletContext()
            register(
                AppConfig::class.java,
                SecurityConfig::class.java,
            )
            refresh()
        }
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
            .build()
    }

    private val securedEndpoint = "/secured"

    @Test
    fun `when user tries to access public endpoint - then return 2xx`() {
        mockMvc.perform(get("/"))
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `when user has token in header - then return 2xx`() {
        val token = jwtUtil.generateToken("user")

        mockMvc.perform(get(securedEndpoint)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token"))
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `when user has token in cookie - then return 2xx`() {
        val token = jwtUtil.generateToken("user")
        val jwtCookie = MockCookie("jwt-token", token)

        mockMvc.perform(get(securedEndpoint)
            .cookie(jwtCookie))
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `when user is not authenticated and has no JWT token - then redirect`() {
        mockMvc.perform(get(securedEndpoint))
            .andExpect(status().is3xxRedirection)
    }
}
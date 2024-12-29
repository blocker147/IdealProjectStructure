package com.example.security

import com.example.security.jwt.JwtUtil
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockCookie
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

// todo: switch from SpringBootTest
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val securedEndpoint = "/jwt-secured"

    @Test
    fun `when unathenticated user with no JWT token tries to access public endpoint - then return 2xx`() {
        mockMvc.perform(get("/"))
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `when user has token in header - then return 2xx`() {
        val token = JwtUtil.generateToken("user")

        mockMvc.perform(get(securedEndpoint)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token"))
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `when user has token in cookie - then return 2xx`() {
        val token = JwtUtil.generateToken("user")
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

    // todo: test for OAuth2.0
    @Test
    @Disabled
    @WithMockUser(username = "user", roles = ["USER"])
    fun `when user is authenticated but has no JWT token - then `() {
        mockMvc.perform(get("/oauth2/authorization/github"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/"))
            .andExpect(cookie().exists("jwt-token"))
            .andExpect(header().exists("Authorization"))
    }
}
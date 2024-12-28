package com.example.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class GitHubOAuthService(
    @Value("\${my-client-id}") private val clientId: String,
    @Value("\${my-client-secret}") private val clientSecret: String
) {
    private val githubTokenUrl = "https://github.com/login/oauth/access_token"
    private val githubUserApiUrl = "https://api.github.com/user"
    private val restTemplate = RestTemplate()

    // fixme: potentially better to use .oauth2Login() in SecurityConfig
    fun exchangeCodeForAccessToken(code: String): String {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            accept = listOf(MediaType.APPLICATION_JSON)
        }
        val body = mapOf(
            "client_id" to clientId,
            "client_secret" to clientSecret,
            "code" to code
        )
        val entity = HttpEntity(body, headers)
        val response = restTemplate.postForEntity(githubTokenUrl, entity, Map::class.java)
        return response.body?.get("access_token") as? String
            ?: throw IllegalArgumentException("Invalid response from GitHub")
    }

    fun fetchUserData(accessToken: String): Map<String, Any?> {
        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $accessToken")
        }
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange(
            githubUserApiUrl,
            HttpMethod.GET,
            entity,
            Map::class.java
        )
        return response.body as Map<String, Any?>
            ?: throw IllegalArgumentException("Failed to fetch user data")
    }
}

package com.example.security.github

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

// fixme: potentially better to use .oauth2Login() in SecurityConfig
@Service
class GitHubOAuthService(
    @Value("\${my-client-id}") private val clientId: String,
    @Value("\${my-client-secret}") private val clientSecret: String
) {
    private val redirectUri = "http://localhost:8080/login/oauth2/code/github"
    private val githubTokenUrl = "https://github.com/login/oauth/access_token"
    private val githubAuthUrl = "https://github.com/login/oauth/authorize"
    private val githubUserApiUrl = "https://api.github.com/user"

    private val restTemplate = RestTemplate()

    fun redirectUrl(): String {
        val url = "$githubAuthUrl?client_id=$clientId&redirect_uri=$redirectUri&scope=read:user"
        return url
    }

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

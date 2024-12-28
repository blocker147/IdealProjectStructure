package com.example.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
class AuthController(
    private val gitHubOAuthService: GitHubOAuthService,
    @Value("\${my-client-id}") private val clientId: String,
) {
    private val redirectUri = "http://localhost:8080/login/oauth2/code/github"
    private val githubAuthUrl = "https://github.com/login/oauth/authorize"

    @GetMapping("/auth-with-github")
    fun startOAuth(): RedirectView {
        val url = "$githubAuthUrl?client_id=$clientId&redirect_uri=$redirectUri&scope=read:user"
        return RedirectView(url)
    }

    @GetMapping("/login/oauth2/code/github")
    fun handleCallback(@RequestParam code: String): Map<String, Any?> {
        val accessToken = gitHubOAuthService.exchangeCodeForAccessToken(code)
        val user = gitHubOAuthService.fetchUserData(accessToken)
        val jwtToken = JwtUtil.generateToken(user["login"] as String)

        return mapOf(
            "username" to user["login"],
            "jwt" to jwtToken
        )
    }

    @GetMapping("/jwt-secured")
    fun jwtSecured(authentication: Authentication): String {
        return "This is a secured endpoint"
    }
}

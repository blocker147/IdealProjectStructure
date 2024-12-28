package com.example.security.github

import com.example.security.jwt.JwtUtil
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
class AuthController(
    private val gitHubOAuthService: GitHubOAuthService,
) {
    @GetMapping("/auth-with-github")
    fun startOAuth(): RedirectView {
        val url = gitHubOAuthService.redirectUrl()
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
}
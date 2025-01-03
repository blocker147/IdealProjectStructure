package com.example.spring.security.jwt

import com.example.spring.security.config.AuthenticationUtils
import com.example.spring.security.jwt.JWTType.ACCESS_TOKEN
import com.example.spring.security.jwt.JWTType.REFRESH_TOKEN
import com.example.spring.security.utils.HTTPUtils
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver

@Component
class JWTFilter(
    private val jwtService: JWTService,
    @Qualifier("handlerExceptionResolver") private val resolver: HandlerExceptionResolver,
) : OncePerRequestFilter() {

    companion object {
        private val STATIC_RESOURCES = arrayOf(".css", ".js", ".png")
        private val FILTERED_PATHS = arrayOf("/", "/favicon.ico", "/error")
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        if (STATIC_RESOURCES.any { path.endsWith(it) }) return true
        if (FILTERED_PATHS.any { path == it }) return true
        return super.shouldNotFilter(request)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        (tryAuthenticateWithAccessToken(request) ?: tryRenewAccessTokenUsingRefreshToken(request, response))?.let {
            AuthenticationUtils.authenticate(it, emptyList())
            filterChain.doFilter(request, response)
            return
        }
        redirectOrReturnErrorMessage(request, response)
    }

    /**
     * @return username if access token is valid, otherwise null
     * */
    private fun tryAuthenticateWithAccessToken(request: HttpServletRequest): String? {
        val accessToken = HTTPUtils.getJWT(request, ACCESS_TOKEN)
        return accessToken?.let {
            jwtService.verifyJWT(accessToken, ACCESS_TOKEN)
        }
    }

    /**
     * If refresh token is valid then new access token is generated and added to response.
     * Else, access and refresh token cookies are cleared from cookies.
     * And refresh token is added to blacklist.
     * @return username if refresh token is valid, otherwise null
     * */
    private fun tryRenewAccessTokenUsingRefreshToken(request: HttpServletRequest, response: HttpServletResponse): String? {
        val refreshToken: String? = HTTPUtils.getJWT(request, REFRESH_TOKEN)
        return refreshToken?.let {
            val username = jwtService.verifyJWT(refreshToken, REFRESH_TOKEN)
            if (username == null) {
                HTTPUtils.clearJWTCookies(response)
                jwtService.blackListJWT(refreshToken)
            } else {
                val accessToken = jwtService.generateJWT(username, ACCESS_TOKEN)
                HTTPUtils.addJWTResponse(request, response, ACCESS_TOKEN, accessToken)
            }
            username
        }
    }

    /**
     * If request is made from browser then user will be redirected to authenticate page.
     * Otherwise, user will receive 401 Unauthorized response.
     * */
    private fun redirectOrReturnErrorMessage(request: HttpServletRequest, response: HttpServletResponse) {
        if (HTTPUtils.isRequestFromBrowser(request)) {
            HTTPUtils.redirect(response, "/")
        } else {
            val exception = InsufficientAuthenticationException("Access token is expired or invalid. "
                    + "Authenticate again using OAuth2 or provide refresh token to receive new access token.")
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            resolver.resolveException(request, response, null, exception)
        }
    }
}
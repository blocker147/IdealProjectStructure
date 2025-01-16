package com.example.spring.security.config

import com.example.spring.utils.HTTPUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExceptionResolver

private val log = KotlinLogging.logger {}

@Component
class AfterOAuth2FailedAuthentication(
    @Qualifier("handlerExceptionResolver") @Autowired val resolver: HandlerExceptionResolver
) : AuthenticationFailureHandler {

    override fun onAuthenticationFailure(request: HttpServletRequest, response: HttpServletResponse, exception: AuthenticationException) {
        log.warn { "OAuth2 authentication failed. ${exception.message}" }

        val isRequestFromBrowser = HTTPUtils.isRequestFromBrowser(request)

        if (isRequestFromBrowser) {
            HTTPUtils.redirect(response, "/")
        } else {
            resolver.resolveException(request, response, null, exception)
        }
    }
}
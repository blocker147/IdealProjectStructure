package com.example.spring.security.controller

import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SecuredController {
    @GetMapping("/jwt-secured")
    fun jwtSecured(authentication: Authentication): String {
        return "This is a secured endpoint"
    }
}
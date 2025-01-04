package com.example.domain.exceptions

class ApplicationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
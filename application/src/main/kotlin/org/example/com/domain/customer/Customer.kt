package org.example.com.domain.customer

data class Customer(
    val id: String? = null,
    val name: String,
    val age: Int,
    val productIds: MutableSet<String>
)

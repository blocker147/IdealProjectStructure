package com.example.domain.customers

interface CustomerService {
    fun createCustomer(customer: Customer): Customer
    fun addProductToCustomer(customerId: String, productId: String): Customer
}
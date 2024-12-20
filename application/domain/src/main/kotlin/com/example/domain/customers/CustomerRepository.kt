package com.example.domain.customers

interface CustomerRepository {
    fun saveCustomer(customer: Customer): Customer
    fun findById(id: String): Customer
    fun updateCustomer(id: String, customer: Customer): Customer
}
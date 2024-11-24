package com.example.customers

interface CustomerRepository {
    fun saveCustomer(customer: Customer): Customer
    fun findById(id: String): Customer
    fun updateCustomer(id: String, customer: Customer): Customer
}
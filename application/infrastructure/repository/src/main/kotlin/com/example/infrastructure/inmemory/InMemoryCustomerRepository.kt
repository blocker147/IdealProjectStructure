package com.example.infrastructure.inmemory

import com.example.domain.customers.Customer
import com.example.domain.customers.CustomerRepository
import java.util.UUID

class InMemoryCustomerRepository : CustomerRepository {
    private val customers: MutableMap<String, Customer> = hashMapOf()

    /** Saves a customer */
    override fun saveCustomer(customer: Customer): Customer {
        val id = UUID.randomUUID().toString()
        val savedCustomer = customer.copy(id = id)
        customers[id] = savedCustomer
        return savedCustomer
    }

    /** Finds a customer by id */
    override fun findById(id: String): Customer {
        return customers[id] ?: throw IndexOutOfBoundsException("No such customer with id: $id")
    }

    override fun updateCustomer(id: String, customer: Customer): Customer {
        customers[id] = customer
        return customer
    }
}
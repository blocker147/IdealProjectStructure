package org.example.com.domain.customer

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CustomerRepository {
    private val customers: MutableMap<String, Customer> = hashMapOf()

    /** Saves a customer */
    fun saveCustomer(customer: Customer): Customer {
        val id = UUID.randomUUID().toString()
        val savedCustomer = customer.copy(id = id)
        customers[id] = savedCustomer
        return savedCustomer
    }

    /** Finds a customer by id */
    fun findById(id: String): Customer {
        return customers[id] ?: throw IndexOutOfBoundsException("No such customer with id: $id")
    }

    fun updateCustomer(id: String, customer: Customer): Customer {
        customers[id] = customer
        return customer
    }
}
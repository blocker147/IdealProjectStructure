package com.example.customers

import com.example.products.ProductService

class CustomerService(
    private val customerRepository: CustomerRepository,
    private val productService: ProductService
) {
    /** Attempts to create a customer */
    fun createCustomer(customer: Customer): Customer {
        return customerRepository.saveCustomer(customer)
    }

    /** Attempts to add product to a customer */
    fun addProductToCustomer(customerId: String, productId: String): Customer {
        val customer = customerRepository.findById(customerId)
        productService.decreaseProductCount(productId, 1)
        customer.productIds.add(productId)
        return customerRepository.updateCustomer(customerId, customer)
    }
}

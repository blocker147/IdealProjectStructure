package com.example.domain.customers

import com.example.domain.products.ProductService

class CustomerServiceImpl(
    private val customerRepository: CustomerRepository,
    private val productService: ProductService
): CustomerService {
    /** Attempts to create a customer */
    override fun createCustomer(customer: Customer): Customer {
        return customerRepository.saveCustomer(customer)
    }

    /** Attempts to add product to a customer */
    override fun addProductToCustomer(customerId: String, productId: String): Customer {
        val customer = customerRepository.findById(customerId)
        productService.decreaseProductCount(productId, 1)
        customer.productIds.add(productId)
        return customerRepository.updateCustomer(customerId, customer)
    }
}

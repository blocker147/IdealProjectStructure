package com.example

import com.example.customers.CustomerRepository
import com.example.customers.CustomerService
import com.example.products.ProductService
import com.example.products.ProductValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfiguration {
    @Bean
    fun productService(
        productValidator: ProductValidator,
        productRepository: InMemoryProductRepository
    ) = ProductService(productValidator, productRepository)

    @Bean
    fun productValidator() = ProductValidator()

    @Bean
    fun productRepository() = InMemoryProductRepository()

    @Bean
    fun customerService(
        customerRepository: CustomerRepository,
        productService: ProductService
    ) = CustomerService(customerRepository, productService)

    @Bean
    fun customerRepository() = InMemoryCustomerRepository()
}
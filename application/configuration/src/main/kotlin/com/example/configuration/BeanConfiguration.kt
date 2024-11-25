package com.example.configuration

import com.example.domain.customers.CustomerRepository
import com.example.domain.customers.CustomerService
import com.example.domain.customers.CustomerServiceImpl
import com.example.domain.products.ProductRepository
import com.example.domain.products.ProductService
import com.example.domain.products.ProductServiceImpl
import com.example.domain.products.ProductValidator
import com.example.infrastructure.InMemoryCustomerRepository
import com.example.infrastructure.InMemoryProductRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfiguration {
    @Bean
    fun productService(
        productValidator: ProductValidator,
        productRepository: ProductRepository
    ): ProductService {
        return ProductServiceImpl(productValidator, productRepository)
    }

    @Bean
    fun productValidator(): ProductValidator {
        return ProductValidator()
    }

    @Bean
    fun productRepository(): ProductRepository {
        return InMemoryProductRepository()
    }

    @Bean
    fun customerService(
        customerRepository: CustomerRepository,
        productService: ProductService,
    ): CustomerService {
        return CustomerServiceImpl(customerRepository, productService)
    }

    @Bean
    fun customerRepository(): CustomerRepository {
        return InMemoryCustomerRepository()
    }
}
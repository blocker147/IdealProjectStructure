package com.example.configuration

import com.example.domain.customers.CustomerRepository
import com.example.domain.customers.CustomerService
import com.example.domain.customers.CustomerServiceImpl
import com.example.domain.products.ProductRepository
import com.example.domain.products.ProductService
import com.example.domain.products.ProductServiceImpl
import com.example.domain.products.ProductValidator
import com.example.domain.products.ProductValidatorImpl
import com.example.features.products.usecase.ProductMapper
import com.example.features.products.usecase.ProductUseCaseImpl
import com.example.features.products.usecase.ProductsUseCase
import com.example.infrastructure.inmemory.InMemoryCustomerRepository
import com.example.infrastructure.mongo.ProductDao
import com.example.infrastructure.mongo.ProductRepositoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

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
        return ProductValidatorImpl()
    }

    @Bean
    fun productRepository(
        productDao: ProductDao,
        clock: Clock,
    ): ProductRepository {
        return ProductRepositoryImpl(productDao, clock)
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

    @Bean
    fun productsUseCase(
        productService: ProductService,
        productMapper: ProductMapper,
    ): ProductsUseCase {
        return ProductUseCaseImpl(productService, productMapper)
    }

    @Bean
    fun productMapper(): ProductMapper {
        return ProductMapper()
    }

    @Bean
    fun clock(): Clock {
        return Clock.systemDefaultZone()
    }
}
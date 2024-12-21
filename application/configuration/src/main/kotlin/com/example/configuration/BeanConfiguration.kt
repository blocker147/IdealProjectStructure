package com.example.configuration

import com.example.domain.customers.CustomerRepository
import com.example.domain.customers.CustomerService
import com.example.domain.customers.CustomerServiceImpl
import com.example.domain.products.*
import com.example.features.products.usecase.ProductMapper
import com.example.features.products.usecase.ProductUseCaseImpl
import com.example.features.products.usecase.ProductsUseCase
import com.example.infrastructure.client.productnutrition.ProductNutritionClientImpl
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
        productRepository: ProductRepository,
        productNutritionClient: ProductNutritionClient,
    ): ProductService {
        return ProductServiceImpl(productValidator, productRepository, productNutritionClient)
    }

    @Bean
    fun productNutritionClient(): ProductNutritionClient {
        return ProductNutritionClientImpl()
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
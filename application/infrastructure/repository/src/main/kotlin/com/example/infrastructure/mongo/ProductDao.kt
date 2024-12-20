package com.example.infrastructure.mongo

import org.springframework.data.mongodb.repository.MongoRepository

interface ProductDao : MongoRepository<Product, String> {
    fun findByTitle(title: String): Product
}
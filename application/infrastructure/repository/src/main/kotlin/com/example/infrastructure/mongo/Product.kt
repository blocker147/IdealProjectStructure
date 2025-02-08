package com.example.infrastructure.mongo

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "products")
data class Product(
    @Id val id: String? = null,
    @CreatedDate val createdDate: Instant? = null,
    @LastModifiedDate val lastModifiedDate: Instant? = null,

    @Indexed(unique = true, useGeneratedName = true)
    val generatedId: Long,
    val title: String,
    val count: Int = 0,
)
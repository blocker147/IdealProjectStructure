package com.example.infrastructure.mongo

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProductDaoTest : AbstractMongoTest() {
    private lateinit var target: ProductDao

    @BeforeEach
    fun setup() {
        target = repositoryFactory.getRepository(
            ProductDao::class.java
        )
    }

    @Test
    fun `when attempting to find by title - return document`() {
        val expected = Product(title = "Apple", count = 10)
        target.save(expected)

        val actual = target.findByTitle("Apple")

        actual.title shouldBe expected.title
        actual.count shouldBe expected.count
    }
}
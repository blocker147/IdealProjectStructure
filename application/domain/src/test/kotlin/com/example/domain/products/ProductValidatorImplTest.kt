package com.example.domain.products

import com.example.domain.exceptions.ApplicationException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ProductValidatorImplTest {
    private val target = ProductValidatorImpl()

    @Test
    fun `when product is valid - then do not throw an exception`() {
        val product = ProductFactory.createProduct(title = "Title", count = 1)

        target.validate(product)
    }

    @Test
    fun `when product has wrong count - then throw exception`() {
        val product = ProductFactory.createProduct(count = 0)

        val exception = shouldThrow<ApplicationException> { target.validate(product) }

        exception.message shouldBe "Wrong count: 0"
    }

    @Test
    fun `when product has wrong title - then throw exception`() {
        val product = ProductFactory.createProduct(title = "-")

        val exception = shouldThrow<ApplicationException> { target.validate(product) }

        exception.message shouldBe "Wrong title length: 1"
    }
}
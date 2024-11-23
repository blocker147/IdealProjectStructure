package org.example.com.domain.product

import org.springframework.stereotype.Component

@Component
class ProductValidator {
    /** Validates product */
    fun validate(product: Product) {
        val title = product.title
        if (title.isEmpty() || title.length < 3 || title.length > 10) {
            throw IllegalArgumentException("Wrong title length: ${title.length}")
        }

        val count = product.count
        if (count <= 0) {
            throw IllegalArgumentException("Wrong count: $count")
        }
    }
}
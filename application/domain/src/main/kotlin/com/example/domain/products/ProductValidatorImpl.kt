package com.example.domain.products

import com.example.domain.exceptions.ApplicationException

class ProductValidatorImpl : ProductValidator {
    override fun validate(product: Product) {
        val title = product.title
        if (title.isEmpty() || title.length < 3 || title.length > 10) {
            throw ApplicationException("Wrong title length: ${title.length}")
        }

        val count = product.count
        if (count <= 0) {
            throw ApplicationException("Wrong count: $count")
        }
    }
}
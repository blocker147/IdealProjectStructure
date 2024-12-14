package com.example.features.products.usecase

import com.example.features.products.models.Product as ProductModel
import com.example.domain.products.Product as ProductDomain

class ProductMapper {
    fun mapToDomain(product: ProductModel): ProductDomain {
        return ProductDomain(
            id = product.id,
            title = product.title,
            count = product.count,
        )
    }

    fun mapToModel(product: ProductDomain): ProductModel {
        return ProductModel(
            id = product.id,
            title = product.title,
            count = product.count,
        )
    }
}
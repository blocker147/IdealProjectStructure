package com.example.features.products.usecase

import com.example.domain.products.ProductService
import com.example.features.products.models.Product as ProductModel

class ProductUseCaseImpl(
    private val productService: ProductService,
    private val productMapper: ProductMapper,
) : ProductsUseCase {
    override fun createProduct(product: ProductModel): ProductModel {
        val productDomain = productMapper.mapToDomain(product)
        val createdProductDomain = productService.createProduct(productDomain)
        return productMapper.mapToModel(createdProductDomain)
    }

    override fun getProductById(id: String): ProductModel {
        val foundProductDomain = productService.getProductById(id)
        return productMapper.mapToModel(foundProductDomain)
    }
}
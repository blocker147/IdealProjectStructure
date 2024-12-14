package com.example.features.products.rest

import com.example.features.products.api.ProductsApi
import com.example.features.products.models.Product as ProductModel
import com.example.features.products.usecase.ProductsUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductsController(
    private val productsUseCase: ProductsUseCase,
) : ProductsApi {
    override fun createProduct(product: ProductModel): ResponseEntity<ProductModel> {
        val productModel = productsUseCase.createProduct(product)
        return ResponseEntity.status(HttpStatus.CREATED).body(productModel)
    }

    override fun getProductById(id: String): ResponseEntity<ProductModel> {
        val productModel = productsUseCase.getProductById(id)
        return ResponseEntity.status(HttpStatus.OK).body(productModel)
    }
}
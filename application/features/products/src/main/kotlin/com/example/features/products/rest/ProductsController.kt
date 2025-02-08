package com.example.features.products.rest

import com.example.features.products.api.ProductsApi
import com.example.features.products.models.ErrorResponse
import com.example.features.products.models.ProductPage
import com.example.features.products.models.Product
import com.example.features.products.usecase.ProductsUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import java.time.Clock

@RestController
class ProductsController(
    private val productsUseCase: ProductsUseCase,
    private val clock: Clock,
) : ProductsApi {
    override fun createProduct(product: Product): ResponseEntity<Product> {
        val createdProduct = productsUseCase.createProduct(product)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct)
    }

    override fun getProductById(id: String): ResponseEntity<Product> {
        val foundProduct = productsUseCase.getProductById(id)
        return ResponseEntity.status(HttpStatus.OK).body(foundProduct)
    }

    override fun getProducts(minCount: Int?, maxCount: Int?, limit: Int, cursor: String?): ResponseEntity<ProductPage> {
        val foundProductPage = productsUseCase.getProducts(minCount, maxCount, limit, cursor)
        return ResponseEntity.status(HttpStatus.OK).body(foundProductPage)
    }

    @ExceptionHandler(IndexOutOfBoundsException::class)
    fun handleIndexOutOfBoundsException(exception: IndexOutOfBoundsException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(
                404,
                clock.instant().toString(),
                exception.message ?: "Message not specified",
            )
        )
    }
}
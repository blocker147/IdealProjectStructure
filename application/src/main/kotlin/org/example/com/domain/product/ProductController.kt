package org.example.com.domain.product

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
@ControllerAdvice
class ProductController(
    private val productService: ProductService
) {
    @PostMapping
    fun createProduct(@RequestBody product: Product): Response {
        val createdProduct = productService.createProduct(product)
        return Response(
            HttpStatus.CREATED.value(),
            createdProduct,
            System.currentTimeMillis()
        )
    }

    @GetMapping("/search")
    fun searchForProduct(@RequestParam id: String): Response {
        val foundProduct = productService.getProductById(id)
        return Response(
            HttpStatus.OK.value(),
            foundProduct,
            System.currentTimeMillis()
        )
    }
}

data class Response(val code: Int, val product: Product, val timestamp: Long)
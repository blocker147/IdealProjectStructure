package com.example.infrastructure.client.productnutrition

import com.example.domain.products.Product as ProductDomain
import com.sun.net.httpserver.HttpServer
import java.io.OutputStream
import java.net.InetSocketAddress

class SimpleProductNutrition {

    fun startServer(port: Int) {
        val server = HttpServer.create(InetSocketAddress(port), 0)

        // Маршрут для получения данных продукта
        server.createContext("/product-nutrition") { exchange ->
            val productId = exchange.requestURI.path.split("/").lastOrNull()
            if (productId == null) {
                exchange.sendResponseHeaders(400, 0)
                exchange.responseBody.use { it.write("Product ID is missing".toByteArray()) }
                return@createContext
            }

//            val product = ProductDomain(productId, "Sample Product", 10) // Пример объекта
//            val jsonResponse = objectMapper.writeValueAsString(product)
            val jsonResponse = """
                {"message":"Hello world!"}
            """.trimIndent()

            exchange.sendResponseHeaders(200, jsonResponse.toByteArray().size.toLong())
            exchange.responseBody.use { it.write(jsonResponse.toByteArray()) }
        }

        println("Server started on port $port")
        server.start()
    }
}

fun main() {
    SimpleProductNutrition().startServer(8081)
}
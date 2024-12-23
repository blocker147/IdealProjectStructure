package com.example.infrastructure.client.productnutrition

import com.example.infrastructure.client.productnutrition.models.ProductNutrition as ProductNutritionClient
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

class SimpleProductNutrition {

    fun startServer(port: Int) {
        val objectMapper = jacksonObjectMapper()
        val server = HttpServer.create(InetSocketAddress(port), 0)

        server.createContext("/product-nutrition") { exchange ->
            val productId = exchange.requestURI.path.split("/").lastOrNull()
            if (productId == null) {
                exchange.sendResponseHeaders(400, 0)
                exchange.responseBody.use { it.write("Product ID is missing".toByteArray()) }
                return@createContext
            }

            val productNutrition = ProductNutritionClient()
                .productId("1")
                .title("Banana")
                .proteins(10)
                .fats(5)
                .carbohydrates(20)
                .calories(100)
            val jsonResponse = objectMapper.writeValueAsString(productNutrition)

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
package com.example.domain.products

data class ProductPage(
    val position: Position<Long>,
    val products: List<Product> = emptyList(),
)
data class Position<FIELD>(
    val first: FIELD? = null,
    val prev: FIELD? = null,
    val self: FIELD,
    val next: FIELD? = null,
    val last: FIELD? = null
)
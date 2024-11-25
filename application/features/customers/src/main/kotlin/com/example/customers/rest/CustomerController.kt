package com.example.customers.rest

import com.example.domain.customers.Customer
import com.example.domain.customers.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/customers")
class CustomerController(
    private val customerService: CustomerService
) {
    @PostMapping
    fun createCustomer(@RequestBody customer: Customer): Response {
        val createdCustomer = customerService.createCustomer(customer)
        return Response(
            HttpStatus.CREATED.value(),
            createdCustomer,
            System.currentTimeMillis()
        )
    }

    @PutMapping("/{customerId}")
    fun addProductToCustomer(@PathVariable customerId: String, @RequestParam productId: String): Response {
        val customer = customerService.addProductToCustomer(customerId, productId)
        return Response(
            HttpStatus.OK.value(),
            customer,
            System.currentTimeMillis()
        )
    }
}

data class Response(val code: Int, val customer: Customer, val timestamp: Long)
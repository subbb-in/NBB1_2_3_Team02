package edu.example.kotlindevelop.domain.orders.orders.controller

import edu.example.kotlindevelop.domain.orders.orders.dto.OrderDTO
import edu.example.kotlindevelop.domain.orders.orders.service.OrderService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(private val orderService: OrderService) {

    @PostMapping
    fun createOrder(
        @RequestBody orderDTO: OrderDTO?,
        @AuthenticationPrincipal user: SecurityUser
    ): ResponseEntity<Map<String, String>> {
        val memberId: Long = user.getId()
        orderDTO?.let {
            orderService.createOrder(it, memberId)
            return ResponseEntity.ok(mapOf("success" to "create"))
        }

        return ResponseEntity.badRequest().body(mapOf("error" to "Invalid order data"))
    }

    @GetMapping("/{orderId}")
    fun getOrder(@PathVariable orderId: Long?): ResponseEntity<OrderDTO> {
        val orderDTO = orderService.read(orderId) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(orderDTO)
    }

    @GetMapping("/list")
    fun getList(
        @Validated pageRequestDTO: OrderDTO.PageRequestDTO?,
        @AuthenticationPrincipal user: SecurityUser
    ): ResponseEntity<Page<OrderDTO.OrderListDTO>> {
        val memberId: Long = user.getId()
        val orders = orderService.getList(pageRequestDTO!!, memberId)
        return ResponseEntity.ok(orders)
    }

    @DeleteMapping("/{orderId}")
    fun deleteOrder(@PathVariable orderId: Long?): ResponseEntity<Map<String, String>> {
        orderService.delete(orderId)
        return ResponseEntity.ok(mapOf("success" to "delete"))
    }

    @GetMapping("/monthly-summary")
    fun getMonthlyOrderSummary(@AuthenticationPrincipal user: SecurityUser): ResponseEntity<List<Map<String, Any?>>> {
        val memberId: Long = user.getId()
        val monthlySummary = orderService.getMonthlyOrderSummary(memberId)
        return ResponseEntity.ok(monthlySummary)
    }

    @GetMapping("/average-prices")
    fun getMonthlyAveragePrices(): ResponseEntity<Map<String, Map<String, Double>>> {
        return ResponseEntity.ok(orderService.getMonthlyAveragePrices())
    }


}
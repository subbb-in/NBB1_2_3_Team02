package edu.example.kotlindevelop.domain.orders.orders.controller

import edu.example.kotlindevelop.domain.orders.orderItem.dto.OrderItemDTO
import edu.example.kotlindevelop.domain.orders.orders.dto.OrderDTO
import edu.example.kotlindevelop.domain.orders.orders.entity.Orders
import edu.example.kotlindevelop.domain.orders.orders.service.OrderService
import edu.example.kotlindevelop.global.security.SecurityUser
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.AbstractPersistable_.id
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
        val memberId: Long = user.id
        orderDTO?.let {
            orderService.createOrder(it, memberId)
            return ResponseEntity.ok(mapOf("success" to "create"))
        }

        return ResponseEntity.badRequest().body(mapOf("error" to "Invalid order data"))
    }


    @GetMapping("/repeat")
    fun putOrderFromPrevMonth(
        @AuthenticationPrincipal user: SecurityUser
    ): ResponseEntity<List<Orders>> {
        val memberId = user.id
        val previousOrders = orderService.getPrevMonthOrders(memberId)
        return ResponseEntity.ok(previousOrders)
    }
//
//        if (previousOrders.isEmpty()) {
//            return ResponseEntity.badRequest().body(mapOf("error" to "전 주문 내역이 없습니다."))
//        }
//        //이전 주문 기반
//        previousOrders.forEach { previousOrder -> // 각 주문에 대해 반복
//            val newOrderDTO = OrderDTO(
//                id = 0L,
//                items = previousOrder.orderItems.map { item -> // OrderItemDTO 변환
//                    OrderItemDTO(
//                        productId = item.product?.id,
//                        quantity = item.quantity,
//                        price = item.price
//                    )
//                },
//                    totalPrice = previousOrder.totalPrice
//                )
//            }
//        return ResponseEntity.ok(mapOf("success" to "Orders created from previous month"))
//    }

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
        val memberId: Long = user.id
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
        val memberId: Long = user.id
        val monthlySummary = orderService.getMonthlyOrderSummary(memberId)
        return ResponseEntity.ok(monthlySummary)
    }

    @GetMapping("/average-prices")
    fun getMonthlyAveragePrices(): ResponseEntity<Map<String, Map<String, Double>>> {
        return ResponseEntity.ok(orderService.getMonthlyAveragePrices())
    }


}
package edu.example.kotlindevelop.domain.orders.orderItem.dto

import edu.example.kotlindevelop.domain.orders.orderItem.entity.OrderItem
import edu.example.kotlindevelop.domain.orders.orders.entity.Orders


class OrderItemDTO {
    private var productId: Long? = null // 상품 ID
    private var quantity = 0 // 수량
    private var price = 0 // 사용자가 입력한 가격

    // DTO -> 엔티티 변환 메서드
    fun toEntity(product: Product?, orders: Orders?): OrderItem {
        return OrderItem.builder()
            .product(product)
            .orders(orders)
            .quantity(this.quantity)
            .price(this.price)
            .build()
    }

    // 엔티티로부터 DTO 생성
    constructor(orderItem: OrderItem) {
        this.productId = orderItem.getProduct().getId()
        this.quantity = orderItem.getQuantity()
        this.price = orderItem.getPrice()
    }

    constructor()
}
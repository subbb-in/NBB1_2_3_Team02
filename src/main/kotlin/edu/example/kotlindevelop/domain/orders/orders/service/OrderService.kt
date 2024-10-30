package edu.example.kotlindevelop.domain.orders.orders.service

import edu.example.kotlindevelop.domain.orders.orderItem.entity.OrderItem
import edu.example.kotlindevelop.domain.orders.orderItem.repository.OrderItemRepository
import edu.example.kotlindevelop.domain.orders.orders.dto.OrderDTO
import edu.example.kotlindevelop.domain.orders.orders.entity.Orders
import edu.example.kotlindevelop.domain.orders.orders.exception.OrderException
import edu.example.kotlindevelop.domain.orders.orders.repository.OrderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository,
    private val orderItemRepository: OrderItemRepository
) {

    @Transactional
    fun createOrder(orderDTO: OrderDTO, memberId: Long?): Orders {
        val member = memberRepository.findById(memberId)
            .orElseThrow { MemberException.MEMBER_NOT_FOUND.getMemberTaskException() }

        val orders = orderDTO.toEntity(member, productRepository) // DTO에서 엔티티로 변환
        return orderRepository.save(orders)
    }

    fun read(orderId: Long?): OrderDTO? {
        val orders = orderId?.let {
            orderRepository.findById(it)
                .orElseThrow { OrderException.NOT_FOUND.get() }
        }

        return orders?.let { OrderDTO(it) }
    }

    @Transactional
    fun delete(orderId: Long?) {
        val orders = orderId?.let {
            orderRepository.findById(it)
                .orElseThrow { OrderException.NOT_FOUND.get() }
        }
        try {
            if (orders != null) {
                orderRepository.delete(orders)
            }
        } catch (e: Exception) {
            throw OrderException.NOT_REMOVED.get()
        }
    }

    fun getList(pageRequestDTO: OrderDTO.PageRequestDTO, memberId: Long?): Page<OrderDTO.OrderListDTO> {
        val sort = Sort.by("id").descending()
        val pageable: Pageable = pageRequestDTO.getPageable(sort)

        val member = memberRepository.findById(memberId)
            .orElseThrow { MemberException.MEMBER_NOT_FOUND.getMemberTaskException() }
        val ordersPage = orderRepository.findByMember(member, pageable)
        return ordersPage!!.map { OrderDTO.OrderListDTO(it) }
    }

    fun getMonthlyOrderSummary(memberId: Long): List<Map<String, Any?>> {
        val member = memberRepository.findById(memberId)
            .orElseThrow { RuntimeException("Member not found") }

        val results = orderRepository.getMonthlyTotalPrice(member) ?: return emptyList()

        return results.mapNotNull { result ->
            if (result != null && result is Array<*>) { // result가 null이 아니고 Array인지 확인
                val orderMonth = result.getOrNull(0) as? String
                val totalPrice = result.getOrNull(1) as? Double

                if (orderMonth != null && totalPrice != null) {
                    mapOf(
                        "orderMonth" to orderMonth,
                        "totalPrice" to totalPrice
                    )
                } else {
                    null
                }
            } else {
                null // result가 null이거나 Array가 아닐 경우 무시
            }
        }
    }

    fun getMonthlyAveragePrices(): Map<String, Map<String, Double>> {
        val orderItems = orderItemRepository.findAll().filterNotNull()

        return orderItems
            .filter { it.orders?.createdAt != null && it.product?.name != null }
            .groupBy { it.orders!!.createdAt!!.month.name }
            .mapValues { (month, itemsByMonth) ->
                itemsByMonth
                    .groupBy { it.product?.name ?: "Unknown Product" } // null일 경우 "Unknown Product"로 대체
                    .mapValues { (_, itemsByProduct) ->
                        itemsByProduct
                            .mapNotNull { it.price?.toDouble()?.div(it.quantity ?: 1) }
                            .average()
                    }
            }
    }
}
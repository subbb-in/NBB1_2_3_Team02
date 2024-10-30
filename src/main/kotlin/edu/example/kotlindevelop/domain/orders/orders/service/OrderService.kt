package edu.example.kotlindevelop.domain.orders.orders.service

import edu.example.kotlindevelop.domain.orders.orders.dto.OrderDTO
import edu.example.kotlindevelop.domain.orders.orders.entity.Orders
import edu.example.kotlindevelop.domain.orders.orders.exception.OrderException
import edu.example.kotlindevelop.domain.orders.orders.repository.OrderRepository
import edu.example.kotlindevelop.domain.orders.orderItem.repository.OrderItemRepository
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

    fun read(orderId: Long?): OrderDTO {
        val orders = orderRepository.findById(orderId)
            .orElseThrow { OrderException.NOT_FOUND.get() }

        return OrderDTO(orders)
    }

    @Transactional
    fun delete(orderId: Long?) {
        val orders = orderRepository.findById(orderId)
            .orElseThrow { OrderException.NOT_FOUND.get() }
        try {
            orderRepository.delete(orders)
        } catch (e: Exception) {
            throw OrderException.NOT_REMOVED.get()
        }
    }

    fun getList(pageRequestDTO: PageRequestDTO, memberId: Long?): Page<OrderListDTO> {
        val sort = Sort.by("id").descending()
        val pageable: Pageable = pageRequestDTO.getPageable(sort)

        val member = memberRepository.findById(memberId)
            .orElseThrow { MemberException.MEMBER_NOT_FOUND.getMemberTaskException() }
        val ordersPage = orderRepository.findByMember(member, pageable)
        return ordersPage.map { OrderListDTO(it) }
    }

    fun getMonthlyOrderSummary(memberId: Long): List<Map<String, Any>> {
        val member = memberRepository.findById(memberId)
            .orElseThrow { RuntimeException("Member not found") }

        val results = orderRepository.getMonthlyTotalPrice(member)

        // 결과를 Map 형태로 변환
        return results.map { result ->
            mapOf(
                "orderMonth" to result[0], // 월
                "totalPrice" to result[1] // 총 금액
            )
        }
    }

    val monthlyAveragePrices: Map<String, Map<String, Double>>
        get() {
            val orderItems = orderItemRepository.findAll()

            // 월별 및 상품별 평균 단가 계산
            return orderItems.groupBy { it.orders.createdAt.month.name }
                .mapValues { entry ->
                    entry.value.groupBy { it.product.name }
                        .mapValues { productEntry ->
                            productEntry.value.map { it.price.toDouble() / it.quantity }.average()
                        }
                }
        }
}

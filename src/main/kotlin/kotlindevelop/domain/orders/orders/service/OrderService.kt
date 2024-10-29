package kotlindevelop.domain.orders.orders.service

import kotlindevelop.domain.orders.orders.dto.OrderDTO
import kotlindevelop.domain.orders.orders.entity.Orders
import kotlindevelop.domain.orders.orders.repository.OrderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Function
import java.util.function.ToDoubleFunction
import java.util.stream.Collectors

@Service
@Transactional(readOnly = true)
@Log4j2
@RequiredArgsConstructor
class OrderService {
    private val orderRepository: OrderRepository? = null
    private val productRepository: ProductRepository? = null
    private val memberRepository: MemberRepository? = null
    private val orderItemRepository: OrderItemRepository? = null

    @Transactional
    fun createOrder(orderDTO: OrderDTO, memberId: Long?): Orders {
        val member: Member = memberRepository.findById(memberId)
            .orElseThrow(MemberException.MEMBER_NOT_FOUND::getMemberTaskException)

        memberRepository.save(member)
        val orders: Orders = orderDTO.toEntity(member, productRepository) // DTO에서 엔티티로 변환
        orderRepository.save(orders)

        return orders
    }

    fun read(orderId: Long?): OrderDTO {
        val orders: Orders = orderRepository.findById(orderId).orElseThrow
        OrderException.NOT_FOUND::get

        return OrderDTO(orders)
    }

    @Transactional
    fun delete(orderId: Long?) {
        val orders: Orders = orderRepository.findById(orderId)
            .orElseThrow(OrderException.NOT_FOUND::get)
        try {
            orderRepository.delete(orders)
        } catch (e: Exception) {
            throw OrderException.NOT_REMOVED.get()
        }
    }

    fun getList(pageRequestDTO: PageRequestDTO, memberId: Long?): Page<OrderListDTO> {
        try {
            val sort = Sort.by("id").descending()
            val pageable: Pageable = pageRequestDTO.getPageable(sort)

            val member: Member = memberRepository.findById(memberId)
                .orElseThrow(MemberException.MEMBER_NOT_FOUND::getMemberTaskException)
            val ordersPage: Page<Orders> = orderRepository.findByMember(member, pageable)
            return ordersPage.map<OrderListDTO>(Function<Orders, OrderListDTO> { OrderDTO.OrderListDTO() })
        } catch (e: Exception) {
            throw OrderException.NOT_REMOVED.get()
        }
    }

    fun getMonthlyOrderSummary(memberId: Long): List<Map<String, Any>> {
        val member: Member = memberRepository.findById(memberId)
            .orElseThrow { RuntimeException("Member not found") } // 예외 처리

        val results: List<Array<Any>> = orderRepository.getMonthlyTotalPrice(member)

        // 결과를 Map 형태로 변환
        return results.stream()
            .map<Map<String, Any>> { result: Array<Any> ->
                val map: MutableMap<String, Any> =
                    HashMap()
                map["orderMonth"] = result[0] // 월
                map["totalPrice"] = result[1] // 총 금액
                map
            }
            .collect(Collectors.toList())
    }

    val monthlyAveragePrices: Map<String, Map<String, Double>>
        get() {
            val orderItems: List<OrderItem> = orderItemRepository.findAll()

            // 월별 및 상품별 평균 단가 계산
            return orderItems.stream()
                .collect(
                    Collectors.groupingBy<Any, Any, Any, Map<Any, Double>>(
                        Function<Any, Any> { item: Any ->
                            item.getOrders().getCreatedAt().getMonth().name()
                        },  // 월 이름
                        Collectors.groupingBy<Any, Any, Any, Double>(
                            Function<Any, Any> { item: Any -> item.getProduct().getName() },  // 상품 이름
                            Collectors.averagingDouble<Any>(ToDoubleFunction<Any> { item: Any -> item.getPrice() as Double / item.getQuantity() }) // 평균 단가 계산
                        )
                    )
                )
        } //    public List<OrderSummaryDTO> getMonthlyOrderSummary(long memberId, String month) {
    //
    //        return orderRepository.getOrdersSummary(month);
    //    }
    //    public List<OrderDTO.OrderListDTO> getpList(long memberId) {
    //        List<Orders> ordersList = orderRepository.findAll(orderRepository.findById(memberId).get().getMember());
    //        List<OrderDTO.OrderListDTO> orderDTOS = new ArrayList<>();
    //        for (Orders order : ordersList) {
    //            orderDTOS.add(new OrderDTO.OrderListDTO(order));
    //        }
    //        return orderDTOS;
    //    }
}
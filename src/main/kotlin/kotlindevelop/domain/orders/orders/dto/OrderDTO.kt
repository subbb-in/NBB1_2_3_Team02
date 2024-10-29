package kotlindevelop.domain.orders.orders.dto

import kotlindevelop.domain.orders.orders.entity.Orders
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.time.LocalDateTime
import java.util.stream.Collectors

// "주문 데이터 전송 객체"
@Data
@NoArgsConstructor
class OrderDTO @Builder constructor(orders: Orders) {
    private val id: Long
    private var items: List<OrderItemDTO>? = ArrayList<OrderItemDTO>()
    private val totalPrice: Long

    // 기존 엔티티로 변환하는 메서드
    fun toEntity(member: Member?, productRepository: ProductRepository): Orders {
        val orders: Orders = Orders(0L, member)
        require(!(items == null || items.isEmpty())) { "Order items cannot be null or empty" }
        for (itemDTO in items) {
            val product: Product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow { RuntimeException("Product not found") }
            orders.addOrderItem(product, itemDTO.getQuantity(), itemDTO.getPrice())
        }
        return orders
    }

    // 엔티티로부터 DTO 생성
    init {
        this.id = orders.getId()
        this.items = orders.getOrderItems().stream()
            .map { OrderItemDTO() } // OrderItemDTO 변환 필요
            .collect(Collectors.toList())
        this.totalPrice = orders.getTotalPrice() // getTotalPrice 메서드 필요
    }

    // 내부 클래스 - 주문 목록을 위한 DTO
    @Data
    class OrderListDTO {
        private var id: Long? = null // 주문 ID
        private var memberId: Long? = null // 회원 ID
        private var totalPrice: Long? = null // 총 가격
        private var createdAt: LocalDateTime? = null // 생성일
        private var modifiedAt: LocalDateTime? = null // 수정일
        private var orderItems: List<OrderItemDTO>? = null // 주문 항목 리스트

        // 엔티티로부터 DTO 생성
        constructor(orders: Orders) {
            this.id = orders.getId()
            this.memberId = orders.getMember().getId()
            this.totalPrice = orders.getTotalPrice()
            this.createdAt = orders.getCreatedAt()
            this.modifiedAt = orders.getModifiedAt()
            this.orderItems = orders.getOrderItems().stream()
                .map { OrderItemDTO() } // OrderItem을 OrderItemDTO로 변환
                .collect(Collectors.toList())
        }

        // 기본 생성자
        constructor()
    }

    // 내부 클래스 - 페이지 요청 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class PageRequestDTO {
        @Builder.Default
        private val page = 1

        @Builder.Default
        private val size = 10

        fun getPageable(sort: Sort?): Pageable {
            val pageNum = if (page < 0) 1 else page - 1
            val sizeNum = if (size <= 10) 10 else size

            return PageRequest.of(pageNum, sizeNum, sort!!)
        }
    }
}
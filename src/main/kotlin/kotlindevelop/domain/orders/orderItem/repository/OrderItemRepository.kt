package kotlindevelop.domain.orders.orderItem.repository

import kotlindevelop.domain.orders.orderItem.entity.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderItemRepository : JpaRepository<OrderItem?, Long?>

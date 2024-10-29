package kotlindevelop.domian.orders.orderItem.repository

import kotlindevelop.domian.orders.orderItem.entity.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderItemRepository : JpaRepository<OrderItem?, Long?>

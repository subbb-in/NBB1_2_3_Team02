package edu.example.kotlindevelop.domain.orders.orderItem.entity


import jakarta.persistence.*
import edu.example.kotlindevelop.domain.orders.orders.entity.Orders
import org.springframework.data.jpa.domain.AbstractPersistable_.id


@Entity
@Table(name = "orderItem")
class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    var orders: Orders,

    val quantity: Int,
    val price: Int
) {


    fun changeOrder(newOrders: Orders) {
        this.orders = newOrders
    }

    override fun toString(): String {
        return "OrderItem(id=$id, product=$product, orders=$orders, quantity=$quantity, price=$price)"
    }
}
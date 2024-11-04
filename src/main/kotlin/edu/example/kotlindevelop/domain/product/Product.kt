package edu.example.kotlindevelop.domain.product

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.orders.orderItem.entity.OrderItem
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "product")
@EntityListeners(AuditingEntityListener::class)
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String,
    var loss: Long? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @ManyToOne
    @JoinColumn(name = "member_id")
    var maker: Member? = null,

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL])
    var orderItems: MutableList<OrderItem> = mutableListOf()
)
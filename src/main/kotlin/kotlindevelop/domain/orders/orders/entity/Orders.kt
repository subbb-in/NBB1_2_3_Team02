package kotlindevelop.domain.orders.orders.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Data
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "orderItems") //순환 참조 방지
@EntityListeners(AuditingEntityListener::class)
class Orders @Builder constructor(private var totalPrice: Long, member: Member) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null


    @CreatedDate
    @Column(name = "created_at", updatable = false) // 생성일은 업데이트하지 않음
    private var createdAt: LocalDateTime? = null

    @LastModifiedDate
    @Column(name = "modified_at") // 수정일
    private var modifiedAt: LocalDateTime? = null


    @ManyToOne // Member와 관계를 설정
    @JoinColumn(name = "member_id") // 외래키 설정
    private var member: Member

    @OneToMany(mappedBy = "orders", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val orderItems: MutableList<OrderItem> = ArrayList<OrderItem>()

    init {
        this.member = member
    }

    fun setMember(member: Member) {
        this.member = member
        member.getOrdersList().add(this)
    }

    fun addOrderItem(product: Product?, quantity: Int, price: Int) {
        val orderItem: OrderItem = OrderItem.builder()
            .orders(this)
            .product(product)
            .quantity(quantity)
            .price(price)
            .build()

        orderItems.add(orderItem)
        this.totalPrice += price.toLong() * quantity
    }
}
package edu.example.kotlindevelop.domain.product.entity

import edu.example.kotlindevelop.domain.member.entity.Member
import jakarta.persistence.*

@Entity
@Table(name = "product")
class Product(
    var name: String,
    @ManyToOne
    @JoinColumn(name = "member_id")
    var maker: Member? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL])
    var lossRates: MutableList<LossRate> = mutableListOf()

    fun addLossRate(lossRate: LossRate) {
        lossRates.add(lossRate)
        lossRate.product = this
    }

//    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL])
//    val orderItems: List<OrderItem> = mutableListOf()
}

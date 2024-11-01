package edu.example.kotlindevelop.domain.product.entity

import edu.example.kotlindevelop.domain.member.entity.Member
import jakarta.persistence.*

@Entity
@Table(name = "product")
class Product(
    var name: String,
    @ManyToOne // 관계 매핑
    @JoinColumn(name = "member_id") // 외래 키 설정
    var maker: Member? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    var lossRateList: MutableList<LossRate> = mutableListOf()

    fun addLossRate(member: Member, loss: Int) {
        val lossRate = LossRate(
            product = this,
            maker = member,
            loss = loss
        )

        this.lossRateList.add(lossRate)
    }

//    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL])
//    val orderItems: List<OrderItem> = mutableListOf()
}

package edu.example.kotlindevelop.domain.product.entity

import edu.example.kotlindevelop.domain.member.entity.Member
import jakarta.persistence.*

@Entity
@Table(name = "product")
class Product(
    val name: String,
    @ManyToOne // 관계 매핑
    @JoinColumn(name = "member_id") // 외래 키 설정
    var maker: Member? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL])
    var lossRateList: MutableList<LossRate> = mutableListOf()

//    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL])
//    val orderItems: List<OrderItem> = mutableListOf()
}

package kotlindevelop.domain.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@Entity
@Table(name = "product")
data class ProjectProduct(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var productId: Long,

    var name: String,

    @ManyToOne
    @JoinColumn(name = "member_id")
    var maker: Member,

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL])
    val orderItems: List<OrderItem> = mutableListOf(),

    @OneToOne
    @JoinColumn(name = "loss_id")
    var loss: Long

    ) {
        fun setMaker(maker: Member){
            this.maker = maker;
            maker.productList.add(this)
        }

        fun changeLoss(newloss: Long) {
            this.loss = newloss
        }
    }

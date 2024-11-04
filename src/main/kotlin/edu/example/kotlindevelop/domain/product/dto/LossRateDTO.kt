package edu.example.kotlindevelop.domain.product.dto

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.product.entity.LossRate
import edu.example.kotlindevelop.domain.product.entity.Product
import jakarta.validation.constraints.NotBlank

class LossRateDTO {

    data class LossRateRequestDTO(
        val productId: Long,

        val loss: Int,

    ){
        // dto -> 엔티티 변환 메서드
        fun toEntity(member: Member, product: Product): LossRate {
            val product = Product(id = productId , maker = member)
            val lossRatevalue = loss ?: 222
            val lossRate = LossRate(
                maker = member,
                product = product,
                loss = lossRatevalue
            )

            return lossRate
        }

    }


}
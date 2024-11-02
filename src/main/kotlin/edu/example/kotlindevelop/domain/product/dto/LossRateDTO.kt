package edu.example.kotlindevelop.domain.product.dto

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.product.entity.LossRate
import edu.example.kotlindevelop.domain.product.entity.Product
import edu.example.kotlindevelop.domain.product.entity.QLossRate.lossRate
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

class LossRateDTO {

    data class LossRateRequestDTO(
        val productId: Long,

        @field:NotBlank
        val loss: Int? =null
    ){
        // dto -> 엔티티 변환 메서드
        fun toEntity(product: Product, member: Member): LossRate {
            return LossRate(
                maker = member,
                product = product,
                loss = this.loss
            )
        }

    }


}
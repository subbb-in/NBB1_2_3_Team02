package edu.example.kotlindevelop.domain.product.dto

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.product.entity.LossRate
import edu.example.kotlindevelop.domain.product.entity.Product
import edu.example.kotlindevelop.domain.product.entity.QLossRate.lossRate
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

class LossRateDTO {

    data class LossRateRequestDTO(

        val id: Long? = null,

        @field:NotBlank
        val loss: Int,

        val recordedAt: LocalDate? = null
    ){

        // 엔티티 -> DTO 변환 생성자
        constructor(lossRate: LossRate) : this(
            id = lossRate.id,
            loss = lossRate.loss,
            recordedAt = lossRate.recordedAt
        )

        // dto -> 엔티티 변환 메서드
        fun toEntity(product: Product, member: Member): LossRate {
            return LossRate(
                maker = member,
                product = product,
                loss = this.loss
            )
        }

    }

    data class LossRateResponseDTO(
        val productId: Long?,
        val lossRate: Int,
        val recordedAt: LocalDate?
    ) {
        constructor(lossRate: LossRate) : this(
            productId = lossRate.product.id,
            lossRate = lossRate.loss,
            recordedAt = lossRate.recordedAt
        )
    }
}
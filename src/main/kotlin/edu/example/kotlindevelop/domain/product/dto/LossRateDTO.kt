package edu.example.kotlindevelop.domain.product.dto

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.product.entity.LossRate
import edu.example.kotlindevelop.domain.product.entity.Product
import jakarta.validation.constraints.NotBlank

class LossRateDTO {

    data class LossRateRequestDTO(
        val productId: Long,

        val loss: Int?,

    ){
        fun toEntity(member: Member, product: Product): LossRate {
            val lossRateValue = loss ?: 222  // 기본값 설정
            val lossRate = LossRate(
                maker = member,
                product = product, // 파라미터로 받은 product를 그대로 사용
                loss = lossRateValue
            )

            // product와 lossRate의 관계 설정
            product.addLossRate(lossRate)

            return lossRate
        }

    }


}
package com.example.devcoursed.domain.product.product.dto

import edu.example.kotlindevelop.domain.product.entity.LossRate
import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.product.entity.Product
import jakarta.validation.constraints.NotBlank
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.time.LocalDate

class ProductDTO(
    product: Product,
    lossRate: LossRate
) {

    data class CreateProductRequestDto(
        val id: Long,

        @field:NotBlank(message = "식재료 이름은 필수 값입니다.")
        val name: String,

        val lossRates: MutableList<LossRateRequestDTO> = mutableListOf()
    ) {
        fun toEntity(member: Member): Product {
            return Product(
                name = name,
                maker = member,
            )
        }
    }

    data class LossRateRequestDTO(

        val id: Long? = null,

        @field:NotBlank
        val loss: Int,

        val recordedAt: LocalDate? = null
    ){
        constructor(product: Product, lossRate: LossRate) : this(
            id = product.id,
            loss = lossRate.loss,
            recordedAt = lossRate.recordedAt
        )

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
        val productName: String,
        val lossRate: Int,
        val recordedAt: LocalDate?
    ) {
        constructor(product: Product, lossRate: LossRate) : this(
            productId = product.id,
            productName = product.name,
            lossRate = lossRate.loss,
            recordedAt = lossRate.recordedAt
        )
    }





//    constructor(product: Product) : this(
//        id = product.id,
//        name = product.name,
//        loss = product.loss
//    )
//
//    fun toEntity(member: Member?): Product {
//        return Product(
//            name = name,
//            loss = loss,
//            maker = member
//        )
//    }

    data class PageRequestDTO(
        private val page: Int = 0,
        private val size: Int = 5,
        private val sortField: String = "id",
        private val sortDirection: String = "ASC",
    ) {
        val pageable: Pageable
            get() {
                val sort = Sort.by(
                    Sort.Direction.fromString(
                        this.sortDirection
                    ), this.sortField
                )
                return PageRequest.of(this.page, this.size, sort)
            }
    }

    data class AverageResponseDTO(
        val dates: List<LocalDate>? = null,
        val personalAverage: List<BigDecimal>? = null,
        val allUsersAverage: List<BigDecimal>? = null
    )
}

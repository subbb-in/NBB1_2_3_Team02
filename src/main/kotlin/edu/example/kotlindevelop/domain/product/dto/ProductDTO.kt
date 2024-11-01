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

        val lossRates: MutableList<LossRateDTO> = mutableListOf()
    ) {
        fun toEntity(member: Member): Product {
            return Product(
                name = name,
                maker = member,
            )
        }
    }

    data class lossRateDTO(
        @field:NotBlank
        val id: Long?,

        @field:NotBlank
        val name: String,

        @field:NotBlank
        val loss: Int,

        @field:NotBlank
        val recordedAt: LocalDate?
    ){
        constructor(product: Product, lossRate: LossRate) : this(
            id = product.id,
            name = product.name,
            loss = lossRate.loss,
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

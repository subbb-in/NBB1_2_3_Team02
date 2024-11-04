package edu.example.kotlindevelop.domain.product.dto

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.product.entity.LossRate
import edu.example.kotlindevelop.domain.product.entity.Product
import jakarta.validation.constraints.NotBlank
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort


class ProductDTO {

    data class CreateProductRequestDto(
        @field:NotBlank(message = "식재료 이름은 필수 값입니다.")
        val name: String,
        val loss: Int?
    ) {
        fun toEntity(member: Member): Product {
            val product = Product(name = name, maker = member)
            val lossRateValue = loss ?: 222
            val lossRate = LossRate(
                maker = member,
                product = product,
                loss = lossRateValue
            )

            product.addLossRate(lossRate)
            return product
        }
    }

    data class ProductResponseDto (
        val productId: Long,
        val productName: String,
        val latestLossRate: Int
    )

    data class PageRequestDto(
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
}

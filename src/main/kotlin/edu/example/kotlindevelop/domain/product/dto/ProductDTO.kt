package com.example.devcoursed.domain.product.product.dto

import edu.example.kotlindevelop.domain.product.entity.LossRate
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
    @field:NotBlank
    val id: Long? = product.id
    @field:NotBlank
    val name: String = product.name

    @field:NotBlank
    val loss: Int = lossRate.loss
    @field:NotBlank
    val recordedAt: LocalDate? = lossRate.recordedAt?.toLocalDate()


    // 1. 사용자에게 상품명, 로스율 데이터를 같이 받게 되는 경우가 있습니다. 상품명은 product, 로스율은 LossRate에 있는데,
    // 이 둘을 어떻게 엮어들어가야 하는지, DTO 스펙을 어떻게 해야하는지?
    // 2. 상위에 배치 or 이너클래스에 배치하는게 더 효율적인지// 1:1 로 리스트에 담아서
    // 서비스에서 스트림으로 사용하는 것이 더 호율적인지 ? 비슷함 날짜파트와 시간파트를 분리시켜서 진행하는 것도 가능할듯
    // groupby를 통해 통계처리를 할 수 있는 방법을
    // LossRateDTO를 맡는 걸로

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
        val page: Int = 0,
        val size: Int = 5,
        val sortField: String = "id",
        val sortDirection: String = "ASC"
    ) {
        val pageable: Pageable
            get() {
                val sort = Sort.by(
                    Sort.Direction.fromString(sortDirection), sortField
                )
                return PageRequest.of(page, size, sort)
            }
    }

    data class AverageResponseDTO(
        val dates: List<LocalDate>? = null,
        val personalAverage: List<BigDecimal>? = null,
        val allUsersAverage: List<BigDecimal>? = null
    )
}

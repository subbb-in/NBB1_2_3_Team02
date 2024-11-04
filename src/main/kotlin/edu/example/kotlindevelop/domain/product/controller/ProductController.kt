package edu.example.kotlindevelop.domain.product.controller

import edu.example.kotlindevelop.domain.product.dto.LossRateDTO
import edu.example.kotlindevelop.domain.product.dto.ProductDTO
import edu.example.kotlindevelop.domain.product.service.ProductService
import edu.example.kotlindevelop.global.security.SecurityUser
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private final val productService: ProductService
) {
    // 식재료 등록
    @PostMapping("/register")
    fun register(
        @AuthenticationPrincipal user: SecurityUser,
        @Validated @RequestBody request: ProductDTO.CreateProductRequestDto
    ) : ResponseEntity<ProductDTO.CreateProductRequestDto> {
        val id: Long = user.id
        return ResponseEntity.ok(productService.insert(request, id))
    }

    // 식재료 추가 등록
    @PostMapping("/loss")
    fun addLoss(
        @AuthenticationPrincipal user: SecurityUser,
        @Validated @RequestBody request: LossRateDTO.LossRateRequestDTO
    ) : ResponseEntity<LossRateDTO.LossRateRequestDTO> {
        val id : Long = user.id
        return ResponseEntity.ok(productService.addLoss(request, id))
    }


}
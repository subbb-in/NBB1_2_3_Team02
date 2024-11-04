package edu.example.kotlindevelop.domain.product.controller

import edu.example.kotlindevelop.domain.product.dto.ProductDTO
import edu.example.kotlindevelop.domain.product.service.ProductService
import edu.example.kotlindevelop.global.security.SecurityUser
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

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

    // 사용자용 상품 목록 전체 조회
    @GetMapping
    fun getList(
        @AuthenticationPrincipal user: SecurityUser,
        request: ProductDTO.PageRequestDto
    ) : ResponseEntity<Page<ProductDTO.ProductResponseDto>> {
        val memberId: Long = user.id
        val productDtoPage: Page<ProductDTO.ProductResponseDto> = productService.getPersonalProducts(request, memberId)
        return ResponseEntity.ok(productDtoPage)
    }


}
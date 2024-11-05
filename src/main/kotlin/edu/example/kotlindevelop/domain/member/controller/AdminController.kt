package edu.example.kotlindevelop.domain.member.controller


import edu.example.kotlindevelop.domain.member.dto.MemberDTO
import edu.example.kotlindevelop.domain.member.service.MemberService
import edu.example.kotlindevelop.domain.product.dto.ProductDTO
import edu.example.kotlindevelop.domain.product.service.ProductService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/adm")
class AdminController (
    private val memberService: MemberService,
    private val productService: ProductService
){


    //모든 회원 정보 조회하기
    @GetMapping("/members/all")
    fun getMembers(@RequestParam(defaultValue = "0") page: Int): ResponseEntity<Page<MemberDTO.Response>> {
        val pageSize = 10
        val pageable: Pageable = PageRequest.of(page, pageSize)
        val responseDto: Page<MemberDTO.Response> = memberService.readAll(pageable)

        return ResponseEntity.ok<Page<MemberDTO.Response>>(responseDto)
    }

    //모든 회원 식재료 정보 조회하기
    @GetMapping("/products/all")
    fun getProducts(pageRequestDTO: ProductDTO.PageRequestDto): ResponseEntity<Page<ProductDTO.ProductResponseDto>> {
        val responseDto: Page<ProductDTO.ProductResponseDto> = productService.getProducts(pageRequestDTO)
        return ResponseEntity.ok(responseDto)
    }

    // 상품 이름 검색
    @GetMapping("/products/search")
    fun searchProducts(
        @RequestParam("keyword") keyword: String,
        pageRequestDTO: ProductDTO.PageRequestDto): ResponseEntity<Page<ProductDTO.ProductResponseDto>> {
        val responseDto: Page<ProductDTO.ProductResponseDto> = productService.searchProducts(keyword, pageRequestDTO)
        return ResponseEntity.ok(responseDto)
    }
}
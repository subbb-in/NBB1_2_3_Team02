//package edu.example.kotlindevelop.domain.product
//
//import edu.example.kotlin develop.global.security.SecurityUser
//import org.springframework.http.ResponseEntity
//import org.springframework.security.core.annotation.AuthenticationPrincipal
//import org.springframework.validation.annotation.Validated
//import org.springframework.web.bind.annotation.PostMapping
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//
//@RestController
//@RequestMapping("/api/v1/products")
//class ProductController {
//    private val productService: ProductService? = null
//
//    // 식재료 등록
//    @PostMapping("/")
//    fun register(
//        @AuthenticationPrincipal user: SecurityUser,
//        @Validated @RequestBody productDTO: ProductDTO?
//    ): ResponseEntity<ProductDTO> {
//        val id: Long = user.getId()
//        return ResponseEntity.ok(productService.insert(productDTO, id))
//    }
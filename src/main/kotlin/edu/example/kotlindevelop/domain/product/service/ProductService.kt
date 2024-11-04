package edu.example.kotlindevelop.domain.product.service

import edu.example.kotlindevelop.domain.member.service.MemberService
import edu.example.kotlindevelop.domain.product.dto.ProductDTO
import edu.example.kotlindevelop.domain.product.entity.ProductProjection
import edu.example.kotlindevelop.domain.product.exception.ProductException
import edu.example.kotlindevelop.domain.product.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val memberService: MemberService
) {
    // 식재료 등록
    @Transactional
    fun insert(dto: ProductDTO.CreateProductRequestDto, id: Long): ProductDTO.CreateProductRequestDto {
        val member = memberService.getMemberById(id)

        productRepository.findByMakerAndName(member, dto.name)?.let {
            throw ProductException.PRODUCT_ALREADY_EXIST.getProductException()
        }

        productRepository.save(dto.toEntity(member))
        return dto
    }

    // 사용자용 목록 전체 조회
    @Transactional(readOnly = true)
    fun getPersonalProducts(dto: ProductDTO.PageRequestDto, memberId: Long): Page<ProductDTO.ProductResponseDto> {
        val pageable: Pageable = dto.pageable
        val productProjections: Page<ProductProjection> = productRepository.findPersonalProducts(memberId, pageable)
        return productProjections.map { projection ->
            ProductDTO.ProductResponseDto(
                productId = projection.getProductId(),
                productName = projection.getProductName(),
                latestLossRate = projection.getLatestLossRate()
            )
        }
    }

    // 사용자용 상품 이름 검색
    @Transactional(readOnly = true)
    fun searchPersonalProducts(keyword: String, dto: ProductDTO.PageRequestDto, memberId: Long): Page<ProductDTO.ProductResponseDto> {
        val pageable: Pageable = dto.pageable
        val productProjections: Page<ProductProjection> = productRepository.findPersonalProductsByKeyword(keyword, memberId, pageable)
        return productProjections.map { projection ->
            ProductDTO.ProductResponseDto(
                productId = projection.getProductId(),
                productName = projection.getProductName(),
                latestLossRate = projection.getLatestLossRate()
            )
        }
    }

    // 관리자용 목록 전체 조회
    @Transactional(readOnly = true)
    fun getProducts(dto: ProductDTO.PageRequestDto): Page<ProductDTO.ProductResponseDto> {
        val pageable: Pageable = dto.pageable
        val productProjections: Page<ProductProjection> = productRepository.findAllProducts(pageable)
        return productProjections.map { projection ->
            ProductDTO.ProductResponseDto(
                productId = projection.getProductId(),
                productName = projection.getProductName(),
                latestLossRate = projection.getLatestLossRate()
            )
        }
    }

    // 관리자용 상품 이름 검색
    @Transactional(readOnly = true)
    fun searchProducts(keyword: String, dto: ProductDTO.PageRequestDto): Page<ProductDTO.ProductResponseDto> {
        val pageable: Pageable = dto.pageable
        val productProjections: Page<ProductProjection> = productRepository.findProductsByKeyword(keyword, pageable)
        return productProjections.map { projection ->
            ProductDTO.ProductResponseDto(
                productId = projection.getProductId(),
                productName = projection.getProductName(),
                latestLossRate = projection.getLatestLossRate()
            )
        }
    }

//    @Transactional
//    fun addLoss(lossRateDTO: ProductDTO.lossRateDTO , memberId : Long): ProductDTO.lossRateDTO{
//        val member: Member = memberService.getMemberById(memberId)
//
//        productRepository.findByMakerAndName(member, productDTO.getName())
//            .ifPresent { product ->
//                throw ProductException.PRODUCT_ALREADY_EXIST.getProductException()
//            }
//
//    }
}
package edu.example.kotlindevelop.domain.product.service

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.member.service.MemberService
import edu.example.kotlindevelop.domain.product.dto.ProductDTO
import edu.example.kotlindevelop.domain.product.exception.ProductException
import edu.example.kotlindevelop.domain.product.repository.ProductRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

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
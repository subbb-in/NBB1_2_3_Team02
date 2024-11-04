package edu.example.kotlindevelop.domain.product.service

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.member.exception.MemberException
import edu.example.kotlindevelop.domain.member.service.MemberService
import edu.example.kotlindevelop.domain.product.dto.LossRateDTO
import edu.example.kotlindevelop.domain.product.dto.ProductDTO
import edu.example.kotlindevelop.domain.product.entity.Product
import edu.example.kotlindevelop.domain.product.exception.ProductException
import edu.example.kotlindevelop.domain.product.repository.LossRateRepository
import edu.example.kotlindevelop.domain.product.repository.ProductRepository
import jakarta.transaction.Transactional
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val memberService: MemberService,
    private val lossRateRepository: LossRateRepository

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

    // 식재료 추가 등록
    @Transactional
    fun addLoss(dto: LossRateDTO.LossRateRequestDTO, memberId : Long): LossRateDTO.LossRateRequestDTO{
        val member: Member = memberService.getMemberById(memberId)

        lossRateRepository.findLatestProductByMakerAndName(memberId, dto.productId)?.let{
            throw ProductException.PRODUCT_NOT_FOUND.getProductException()
        }

        val product = productRepository.findByIdOrNull(dto.productId)!!

        lossRateRepository.save(dto.toEntity(member, product))

        return dto

    }

}
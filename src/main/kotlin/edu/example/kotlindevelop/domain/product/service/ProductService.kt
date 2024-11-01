package edu.example.kotlindevelop.domain.product.service

import com.example.devcoursed.domain.product.product.dto.ProductDTO
import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.member.service.MemberService
import edu.example.kotlindevelop.domain.product.entity.LossRate
import edu.example.kotlindevelop.domain.product.exception.ProductException
import edu.example.kotlindevelop.domain.product.repository.ProductRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class ProductService (
    private val productRepository: ProductRepository,
    private val memberService: MemberService
){
    fun addLoss(lossRateDTO: ProductDTO.lossRateDTO , memberId : Long): ProductDTO.lossRateDTO{
        val member: Member = memberService.getMemberById(memberId)

        productRepository.findByMakerAndName(member, productDTO.getName())
            .ifPresent { product ->
                throw ProductException.PRODUCT_ALREADY_EXIST.getProductException()
            }

    }
    // 제공 값: 아이디, 이름, 로스, 계정값
    // 반환 값 : Product

}
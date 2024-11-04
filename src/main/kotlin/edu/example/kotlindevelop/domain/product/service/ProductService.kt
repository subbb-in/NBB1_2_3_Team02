package edu.example.kotlindevelop.domain.product.service

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.member.service.MemberService
import edu.example.kotlindevelop.domain.product.dto.LossRateDTO
import edu.example.kotlindevelop.domain.product.dto.ProductDTO
import edu.example.kotlindevelop.domain.product.entity.LossRate
import edu.example.kotlindevelop.domain.product.entity.ProductProjection
import edu.example.kotlindevelop.domain.product.entity.QLossRate.lossRate
import edu.example.kotlindevelop.domain.product.exception.ProductException
import edu.example.kotlindevelop.domain.product.repository.LossRateRepository
import edu.example.kotlindevelop.domain.product.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

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
        println("사용자가 입력한 로스값은 ${dto.loss} 이고, 사용자의 상품 아이디는 ${dto.productId}입니다 1")

        val product = productRepository.findByIdOrNull(dto.productId)
            ?: throw ProductException.PRODUCT_NOT_FOUND.getProductException()

        val lossRate = dto.toEntity(member, product)

        product.addLossRate(lossRate)
        lossRateRepository.save(lossRate)

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


    // 그래프 작업
//    fun getAverageStatistics(memberId: Long, name: String, startDate: LocalDate, endDate: LocalDate): List<ProductDTO.AverageResponseDTO> {
//        // 기간별 개인의 평균 로스율 조회
//        val personalAverages: List<Array<Any>> = productRepository.findPersonalAverageByMakerAndName(
//            memberId = memberId,
//            name = name,
//            startDate = startDate,
//            endDate = endDate
//        )
//
//        // 기간별 전체 사용자의 평균 로스율 조회
//        val allUsersAverages: List<Array<Any>> = productRepository.findAverageStatisticsByName(
//            name = name,
//            startDate = startDate,
//            endDate = endDate
//        )
//
//        // 전체 사용자의 평균 로스율을 Map 형태로 변환
//        val allUserAverages: Map<LocalDate, Double> = allUsersAverages.associate {
//            LocalDate.parse(it[0].toString()) to (it[1] as Double)
//        }
//
//        // 결과 DTO에 매핑할 리스트 생성
//        val dates = mutableListOf<LocalDate>()
//        val personalAverageList = mutableListOf<BigDecimal>()
//        val allUserAverageList = mutableListOf<BigDecimal>()
//
//        // 개인 평균 로스율 데이터와 전체 평균 로스율 데이터를 병합
//        for (data in personalAverages) {
//            val date = LocalDate.parse(data[0].toString())
//            val personalAverage = data[1] as BigDecimal
//            val allUserAverage = BigDecimal.valueOf(allUserAverages[date] ?: 0.0)
//
//            dates.add(date)
//            personalAverageList.add(personalAverage)
//            allUserAverageList.add(allUserAverage)
//        }
//
//        // 결과 DTO 생성
//        val responseDTO = ProductDTO.AverageResponseDTO(dates, personalAverageList, allUserAverageList)
//        return listOf(responseDTO)
//
//    }

    // r그래프 수정

    // 상품의 기간별 평균 로스율
    fun getAverageStatistics(memberId: Long, name: String, startDate: LocalDate, endDate: LocalDate): List<ProductDTO.AverageResponseDTO> {
        // 기간별 개인의 평균 로스율
        val personalAverages = productRepository.findPersonalAverageByMakerAndName(memberId, name, startDate, endDate)

        // 기간별 전체 사용자의 평균 로스율
        val allUsersAverages = productRepository.findAverageStatisticsByName(name, startDate, endDate)

        val allUserAverages = allUsersAverages.associate {
            LocalDate.parse(it[0].toString()) to BigDecimal.valueOf(it[1] as Double)
        }

        val dates = mutableListOf<LocalDate>()
        val personalAverageList = mutableListOf<BigDecimal>()
        val allUserAverageList = mutableListOf<BigDecimal>()

        // 결과 DTO 리스트 생성
        for (data in personalAverages) {
            val date = LocalDate.parse(data[0].toString()) // 날짜 파싱
            val personalAverage = BigDecimal.valueOf(data[1] as Double) // 개인 평균 로스율
            val allUserAverage = allUserAverages[date] ?: BigDecimal.ZERO // 전체 평균 로스율

            dates.add(date)
            personalAverageList.add(personalAverage)
            allUserAverageList.add(allUserAverage)
        }

        // 결과 DTO 생성
        return listOf(ProductDTO.AverageResponseDTO(dates, personalAverageList, allUserAverageList))
    }

}
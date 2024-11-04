package edu.example.kotlindevelop.domain.product.repository

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.product.dto.ProductDTO
import edu.example.kotlindevelop.domain.product.entity.Product
import edu.example.kotlindevelop.domain.product.entity.ProductProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ProductRepository : JpaRepository<Product?, Long?> {
    fun findByMakerAndName(member: Member, productName: String?): Product? // 식재료 등록

    // 일치하는 상품명에 대한 개인의 평균 로스율 조회
    @Query("SELECT DATE(l.recordedAt),AVG(l.loss) FROM Product p JOIN LossRate l " +
            "WHERE p.name = :name AND p.maker.id = :memberId AND l.recordedAt BETWEEN :startDate AND :endDate AND l.loss BETWEEN 0 AND 100 GROUP BY DATE(l.recordedAt)")
    fun findPersonalAverageByMakerAndName(
        @Param("memberId") memberId: Long,
        @Param("name") name: String,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Array<Any>>

    // 사용자용 상품 목록 전체 조회
    @Query("""
        SELECT p.id AS productId, p.name AS productName, l.loss AS latestLossRate 
        FROM Product p LEFT JOIN p.lossRates l 
        WHERE p.maker.id = :memberId AND l.recordedAt = (
            SELECT MAX(l2.recordedAt) 
            FROM LossRate l2 
            WHERE l2.product.id = p.id
        ) 
        ORDER BY l.recordedAt DESC
    """)
    fun listAll(memberId: Long, pageable: Pageable): Page<ProductProjection>
}
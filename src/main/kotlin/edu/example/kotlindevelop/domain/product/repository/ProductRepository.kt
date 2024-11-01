package edu.example.kotlindevelop.domain.product.repository

import edu.example.kotlindevelop.domain.product.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductRepository : JpaRepository<Product?, Long?> {

    @Query(
        value = "SELECT * FROM Product p WHERE p.member_id = :memberId AND p.name = :name ORDER BY p.created_at DESC LIMIT 1",
        nativeQuery = true
    )
    fun findLatestProductByMakerAndName(
        @Param("memberId") memberId: Long?,
        @Param("name") name: String?
    ): Optional<Product?>?
}
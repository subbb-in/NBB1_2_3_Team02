package kotlindevelop.domain.orders.orders.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<Orders?, Long?> {
    fun findByMember(member: Member?, pageable: Pageable?): Page<Orders?>?

    @Query(
        "SELECT FORMATDATETIME(o.createdAt, 'yyyy-MM') AS orderMonth, SUM(o.totalPrice) AS totalPrice "
                + "FROM Orders o "
                + "WHERE o.member = :member " // 특정 회원 필터링
                + "GROUP BY FORMATDATETIME(o.createdAt, 'yyyy-MM') "
                + "ORDER BY FORMATDATETIME(o.createdAt, 'yyyy-MM')"
    )
    fun getMonthlyTotalPrice(@Param("member") member: Member?): List<Array<Any?>?>?
} //    @Query("SELECT o FROM Orders o WHERE o.member = :member")
//    List<Orders> findAll(@Param("member") Member member);




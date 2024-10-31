package edu.example.kotlindevelop.domain.member.repository

import edu.example.kotlindevelop.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MemberRepository : JpaRepository<edu.example.kotlindevelop.domain.member.entity.Member, Long>,
    edu.example.kotlindevelop.domain.member.repository.MemberRepositoryCustom {
    fun findByLoginId(loginId: String): Optional<edu.example.kotlindevelop.domain.member.entity.Member>

    fun findByRefreshToken(refreshToken: String): Optional<edu.example.kotlindevelop.domain.member.entity.Member>

    fun findByEmail(email: String): Optional<edu.example.kotlindevelop.domain.member.entity.Member>
    fun findByLoginIdAndEmail(loginId: String, email: String): Optional<edu.example.kotlindevelop.domain.member.entity.Member>
}
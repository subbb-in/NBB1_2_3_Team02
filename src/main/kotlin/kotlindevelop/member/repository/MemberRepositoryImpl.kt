package kotlindevelop.member.repository

import kotlindevelop.member.entity.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import com.querydsl.jpa.impl.JPAQueryFactory
import kotlindevelop.member.entity.QMember

@Repository
class MemberRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : MemberRepositoryCustom {

    override fun searchMembers(pageable: Pageable): Page<Member> {
        val member = QMember.member

        val content = queryFactory
            .selectFrom(member)
            .orderBy(member.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = queryFactory
            .selectFrom(member)
            .fetch()
            .size.toLong() // total을 Long으로 변환

        return PageImpl(content, pageable, total)
    }
}

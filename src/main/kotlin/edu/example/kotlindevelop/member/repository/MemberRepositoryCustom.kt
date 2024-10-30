package edu.example.kotlindevelop.member.repository

import edu.example.kotlindevelop.member.entity.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MemberRepositoryCustom {
    fun searchMembers(pageable: Pageable): Page<Member>
}
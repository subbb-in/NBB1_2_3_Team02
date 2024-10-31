package edu.example.kotlindevelop.domain.member.repository

import edu.example.kotlindevelop.domain.member.entity.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MemberRepositoryCustom {
    fun searchMembers(pageable: Pageable): Page<edu.example.kotlindevelop.domain.member.entity.Member>
}
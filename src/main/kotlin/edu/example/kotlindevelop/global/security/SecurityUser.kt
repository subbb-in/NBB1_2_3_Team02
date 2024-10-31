package edu.example.kotlindevelop.global.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class SecurityUser(
    val id: Long,
    username: String,
    password: String,
    authorities: Collection<GrantedAuthority>
) : User(username, password, authorities) {
    val loginId: String = username
    val authorities: Set<GrantedAuthority> = authorities.toSet()
}
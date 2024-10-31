package edu.example.kotlindevelop.domain.member.dto

interface OAuth2Response {
    val provider: String
    val providerId: String
    val email: String
    val name: String
}
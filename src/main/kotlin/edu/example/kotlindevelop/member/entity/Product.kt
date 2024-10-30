package edu.example.kotlindevelop.member.entity

import jakarta.persistence.*

@Entity
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "member_id")
    var maker: Member? = null // Member와의 관계
)

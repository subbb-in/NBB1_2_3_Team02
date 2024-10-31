package edu.example.kotlindevelop.domain.product.repository

import edu.example.kotlindevelop.domain.product.entity.LossRate
import org.springframework.data.jpa.repository.JpaRepository

interface LossRateRepository : JpaRepository<LossRate, Long> {

}
package edu.example.kotlindevelop.domain.product.repository

import edu.example.kotlindevelop.domain.orders.orders.entity.Orders
import edu.example.kotlindevelop.domain.product.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product?, Long?>
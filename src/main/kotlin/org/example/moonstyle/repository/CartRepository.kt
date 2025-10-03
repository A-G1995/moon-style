package org.example.moonstyle.repository

import org.example.moonstyle.entity.CartEntity
import org.springframework.data.jpa.repository.JpaRepository


interface CartRepository : JpaRepository<CartEntity, Long> {
    fun findByUserId(userId: Int): CartEntity?
}
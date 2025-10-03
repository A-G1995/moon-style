package org.example.moonstyle.repository

import org.example.moonstyle.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository


interface OrderRepository : JpaRepository<OrderEntity, Long> {
    fun findAllByUserIdOrderByCreatedAtDesc(userId: Int): List<OrderEntity>
}
package org.example.moonstyle.service

import org.example.moonstyle.entity.dto.OrderDto

interface OrderService {
    fun checkout(userId: Int): OrderDto
    fun listForUser(userId: Int): List<OrderDto>
    fun getOne(userId: Int, orderId: Long): OrderDto
}
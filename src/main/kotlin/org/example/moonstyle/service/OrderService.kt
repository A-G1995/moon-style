package org.example.moonstyle.service

import org.example.moonstyle.entity.dto.OrderDto

interface OrderService {
    fun checkout(userId: Int): OrderDto
    fun listMyOrders(userId: Int): List<OrderDto>
    fun getOrder(userId: Int, id: Long): OrderDto
}

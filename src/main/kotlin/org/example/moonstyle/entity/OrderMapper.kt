package org.example.moonstyle.entity

import org.example.moonstyle.entity.dto.CartItemDto
import org.example.moonstyle.entity.dto.OrderDto

object OrderMapper {
    fun toDto(order: OrderEntity, includeItems: Boolean): OrderDto {
        val itemsDto = if (!includeItems) {
            emptyList()
        } else {
            order.items.map { oi ->
                CartItemDto(
                    productId = oi.product.id!!.toLong(),
                    title = oi.title,
                    price = oi.price,
                    quantity = oi.quantity,
                    subtotal = oi.price * oi.quantity
                )
            }
        }
        return OrderDto(
            id = order.id!!,
            total = order.totalAmount,
            items = itemsDto,
            createdAt = order.createdAt.toString()
        )
    }
}
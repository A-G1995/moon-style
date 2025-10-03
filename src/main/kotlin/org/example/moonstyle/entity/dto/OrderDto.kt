package org.example.moonstyle.entity.dto

data class OrderDto(
    val id: Long,
    val total: Long,
    val items: List<CartItemDto>,
    val createdAt: String
)
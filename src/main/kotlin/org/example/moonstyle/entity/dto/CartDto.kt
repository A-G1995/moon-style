package org.example.moonstyle.entity.dto

data class CartDto(
    val items: List<CartItemDto>,
    val total: Long
)

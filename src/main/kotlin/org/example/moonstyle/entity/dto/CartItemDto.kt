package org.example.moonstyle.entity.dto

data class CartItemDto(
    val productId: Long,
    val title: String,
    val price: Long,
    val quantity: Int,
    val subtotal: Long
)

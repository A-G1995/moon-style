package org.example.moonstyle.service

import org.example.moonstyle.entity.dto.CartDto
import org.example.moonstyle.entity.dto.CartItemRequest

interface CartService {
    fun getCart(userId: Int): CartDto
    fun addOrUpdate(userId: Int, req: CartItemRequest): CartDto
    fun removeItem(userId: Int, productId: Long): CartDto
    fun clear(userId: Int)
}
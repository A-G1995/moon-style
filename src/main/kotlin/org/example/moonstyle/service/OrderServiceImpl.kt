package org.example.moonstyle.service

import org.example.moonstyle.entity.OrderEntity
import org.example.moonstyle.entity.OrderItemEntity
import org.example.moonstyle.entity.dto.CartItemDto
import org.example.moonstyle.entity.dto.OrderDto
import org.example.moonstyle.repository.CartRepository
import org.example.moonstyle.repository.OrderRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.format.DateTimeFormatter


@Service
class OrderServiceImpl(
    private val cartRepo: CartRepository,
    private val orderRepo: OrderRepository
) : OrderService {
    
    @Transactional
    override fun checkout(userId: Int): OrderDto {
        val cart = cartRepo.findByUserId(userId)
            ?: throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "سبد خرید خالی است"
            )
        if (cart.items.isEmpty())
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "سبد خرید خالی است"
            )
        
        val total = cart.items.sumOf { it.product.price * it.quantity }
        
        val order = orderRepo.save(
            OrderEntity(
                userId = userId,
                totalAmount = total
            )
        )
        
        for (ci in cart.items) {
            order.items.add(
                OrderItemEntity(
                    order = order,
                    product = ci.product,
                    title = ci.product.title,
                    price = ci.product.price,
                    quantity = ci.quantity,
                    subtotal = ci.product.price * ci.quantity.toLong()
                )
            )
        }
        orderRepo.save(order)
        
        // خالی‌کردن سبد
        cart.items.clear()
        cartRepo.save(cart)
        
        val itemsDto = order.items.map {
            CartItemDto(
                productId = it.product.id!!.toLong(),
                title = it.title,
                price = it.price,
                quantity = it.quantity,
                subtotal = it.subtotal
            )
        }
        val createdAtStr = DateTimeFormatter.ISO_INSTANT.format(order.createdAt)
        return OrderDto(id = order.id!!, total = order.totalAmount, items = itemsDto, createdAt = createdAtStr)
    }
}
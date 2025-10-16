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

@Service
class OrderServiceImpl(
    private val orderRepo: OrderRepository,
    private val cartRepo: CartRepository
) : OrderService {
    
    @Transactional
    override fun checkout(userId: Int): OrderDto {
        val cart = cartRepo.findByUserId(userId)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "سبد خرید یافت نشد")
        if (cart.items.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "سبد خرید خالی است")
        }
        
        val order = OrderEntity(userId = userId)
        var total = 0L
        
        // تبدیل آیتم‌های سبد به آیتم‌های سفارش
        cart.items.forEach { ci ->
            val p = ci.product
            val price = p.price
            val qty = ci.quantity
            total += price * qty
            
            val oi = OrderItemEntity(
                product = p,
                title = p.title,
                price = price,
                quantity = qty
            )
            order.addItem(oi) // ⬅️ بک‌رفرنس ست می‌شود: oi.order = order
        }
        order.totalAmount = total
        
        // فقط Order ذخیره می‌شود؛ آیتم‌ها به‌دلیل Cascade.ALL ذخیره می‌شوند
        val saved = orderRepo.save(order)
        
        // خالی کردن سبد
        cart.items.forEach { it.cart = null }
        cart.items.clear()
        cartRepo.save(cart)
        
        return saved.toDto()
    }
    
    @Transactional(readOnly = true)
    override fun listMyOrders(userId: Int): List<OrderDto> =
        orderRepo.findByUserIdOrderByCreatedAtDesc(userId).map { it.toDto() }
    
    @Transactional(readOnly = true)
    override fun getOrder(userId: Int, id: Long): OrderDto {
        val o = orderRepo.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "سفارش یافت نشد")
        }
        if (o.userId != userId)
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "دسترسی غیرمجاز")
        return o.toDto()
    }
    
    // --- مپ‌ها
    private fun OrderEntity.toDto(): OrderDto =
        OrderDto(
            id = this.id!!,
            total = this.totalAmount,
            items = this.items.map {
                CartItemDto(
                    productId = it.product.id!!.toLong(),
                    title = it.title,
                    price = it.price,
                    quantity = it.quantity,
                    subtotal = it.price * it.quantity
                )
            },
            createdAt = this.createdAt.toInstant().toString()
        )
}

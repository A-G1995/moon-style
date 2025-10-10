package org.example.moonstyle.service

import org.example.moonstyle.entity.OrderEntity
import org.example.moonstyle.entity.OrderItemEntity
import org.example.moonstyle.entity.OrderMapper
import org.example.moonstyle.entity.dto.OrderDto
import org.example.moonstyle.repository.CartRepository
import org.example.moonstyle.repository.OrderRepository
import org.example.moonstyle.repository.ProductRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.OffsetDateTime


@Service
class OrderServiceImpl(
    private val orderRepo: OrderRepository,
    private val cartRepo: CartRepository,
    private val productRepo: ProductRepository
) : OrderService {
    
    @Transactional
    override fun checkout(userId: Int): OrderDto {
        val cart = cartRepo.findByUserId(userId)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "سبد خالی است")
        if (cart.items.isEmpty()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "سبد خالی است")
        
        val prods = productRepo
            .findAllById(cart.items.map { it.product.id!! }.distinct())
            .associateBy { it.id!! }
        
        var total = 0L
        val order = OrderEntity(userId = userId, total = 0L, createdAt = OffsetDateTime.now())
        val orderItems = cart.items.map { ci ->
            val p = prods[ci.product.id!!]!!
            val line = p.price * ci.quantity
            total += line
            OrderItemEntity(
                order = order,
                product = p,
                title = p.title,
                price = p.price,      // Long
                quantity = ci.quantity
            )
        }.toMutableList()
        
        order.items.addAll(orderItems)
        val saved = orderRepo.save(order.copy(total = total))
        
        // خالی کردن سبد
        cart.items.clear()
        cartRepo.save(cart)
        
        return OrderMapper.toDto(saved, includeItems = true)
    }
    
    @Transactional(readOnly = true)
    override fun listForUser(userId: Int): List<OrderDto> =
        orderRepo.findByUserIdOrderByCreatedAtDesc(userId)
            .map { OrderMapper.toDto(it, includeItems = false) }
    
    @Transactional(readOnly = true)
    override fun getOne(userId: Int, orderId: Long): OrderDto {
        val o = orderRepo.findById(orderId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "سفارش یافت نشد")
        }
        if (o.userId != userId) throw ResponseStatusException(HttpStatus.FORBIDDEN, "دسترسی غیرمجاز")
        return OrderMapper.toDto(o, includeItems = true)
    }
}
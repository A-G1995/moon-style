package org.example.moonstyle.service

import org.example.moonstyle.entity.CartEntity
import org.example.moonstyle.entity.CartItemEntity
import org.example.moonstyle.entity.dto.CartDto
import org.example.moonstyle.entity.dto.CartItemDto
import org.example.moonstyle.entity.dto.CartItemRequest
import org.example.moonstyle.repository.CartRepository
import org.example.moonstyle.repository.ProductRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class CartServiceImpl(
    private val cartRepo: CartRepository,
    private val productRepo: ProductRepository
) : CartService {
    
    @Transactional(readOnly = true)
    override fun getCart(userId: Int): CartDto {
        val cart = cartRepo.findByUserId(userId) ?: return CartDto(emptyList(), 0)
        val items = cart.items.map { it ->
            val price = it.product.price // Long
            CartItemDto(
                productId = it.product.id!!.toLong(),
                title = it.product.title,
                price = price,
                quantity = it.quantity,
                subtotal = price * it.quantity
            )
        }
        val total = items.sumOf { it.subtotal }
        return CartDto(items, total)
    }
    
    @Transactional
    override fun addOrUpdate(userId: Int, req: CartItemRequest): CartDto {
        if (req.quantity <= 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "تعداد نامعتبر است")
        
        val product = productRepo.findById(req.productId.toInt()).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "محصول یافت نشد")
        }
        
        val cart = cartRepo.findByUserId(userId) ?: cartRepo.save(CartEntity(userId = userId))
        
        val existing = cart.items.firstOrNull { it.product.id == product.id }
        if (existing == null) {
            cart.items.add(CartItemEntity(cart = cart, product = product, quantity = req.quantity))
        } else {
            existing.quantity = req.quantity
        }
        cartRepo.save(cart)
        return getCart(userId)
    }
    
    @Transactional
    override fun removeItem(userId: Int, productId: Long): CartDto {
        val cart = cartRepo.findByUserId(userId) ?: return CartDto(emptyList(), 0)
        cart.items.removeIf { it.product.id?.toLong() == productId }
        cartRepo.save(cart)
        return getCart(userId)
    }
    
    @Transactional
    override fun clear(userId: Int) {
        val cart = cartRepo.findByUserId(userId) ?: return
        cart.items.clear()
        cartRepo.save(cart)
    }
}

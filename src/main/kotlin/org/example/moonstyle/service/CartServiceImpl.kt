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
        val cart = cartRepo.findByUserId(userId) ?: return CartDto(emptyList(), 0L)
        
        val items = cart.items.map { ci ->
            val price: Long = ci.product.price
            CartItemDto(
                productId = ci.product.id!!.toLong(),
                title = ci.product.title,
                price = price,
                quantity = ci.quantity,
                subtotal = price * ci.quantity
            )
        }
        
        val total: Long = items.fold(0L) { acc, it -> acc + it.subtotal }
        return CartDto(items, total)
    }
    
    @Transactional
    override fun addOrUpdate(userId: Int, req: CartItemRequest): CartDto {
        val qty = req.quantity.coerceAtLeast(1) // حداقل 1
        val product = productRepo.findById(req.productId.toInt()).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "محصول یافت نشد")
        }
        
        // اگر سبد نبود، بسازیم
        val cart = cartRepo.findByUserId(userId) ?: cartRepo.save(CartEntity(userId = userId))
        
        // آیا آیتم از قبل در سبد هست؟
        val existing = cart.items.firstOrNull { it.product.id == product.id }
        if (existing == null) {
            // ایجاد آیتم جدید و ست کردن بک‌رفرنس‌ها
            val item = CartItemEntity(
                cart = cart,
                product = product,
                quantity = qty
            )
            cart.items.add(item) // برای mappedBy="cart" لازم است
        } else {
            existing.quantity = qty
            // اطمینان از سازگاری رابطه
            if (existing.cart != cart) existing.cart = cart
        }
        
        cartRepo.save(cart) // به‌واسطه‌ی cascade، آیتم‌ها هم ذخیره می‌شوند (اگر در mapping گذاشته باشی)
        return getCart(userId)
    }
    
    @Transactional
    override fun removeItem(userId: Int, productId: Long): CartDto {
        val cart = cartRepo.findByUserId(userId) ?: return CartDto(emptyList(), 0L)
        
        val iter = cart.items.iterator()
        var removed = false
        while (iter.hasNext()) {
            val it = iter.next()
            if (it.product.id?.toLong() == productId) {
                // قطع رابطهٔ دوطرفه تا orphanRemoval درست کار کند
                it.cart = null
                iter.remove()
                removed = true
            }
        }
        
        if (removed) cartRepo.save(cart)
        return getCart(userId)
    }
    
    @Transactional
    override fun clear(userId: Int) {
        val cart = cartRepo.findByUserId(userId) ?: return
        // قطع رابطهٔ دوطرفه برای تمام آیتم‌ها
        cart.items.forEach { it.cart = null }
        cart.items.clear()
        cartRepo.save(cart)
    }
}

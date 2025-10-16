package org.example.moonstyle.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "carts", indexes = [Index(name = "idx_cart_user", columnList = "user_id", unique = true)])
data class CartEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "user_id", nullable = false, unique = true)
    val userId: Int,
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    val createdAt: Date = Date(),
    
    @OneToMany(
        mappedBy = "cart",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val items: MutableList<CartItemEntity> = mutableListOf()
) {
    fun addItem(item: CartItemEntity) {
        item.cart = this
        items.add(item)
    }
}

package org.example.moonstyle.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "orders")
data class OrderEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "user_id", nullable = false)
    val userId: Int,
    
    @Column(name = "total_amount", nullable = false)
    var totalAmount: Long = 0L,
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    val createdAt: Date = Date(),
    
    @OneToMany(
        mappedBy = "order",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val items: MutableList<OrderItemEntity> = mutableListOf()
) {
    /** همیشه بک‌رفرنس آیتم را به این سفارش ست کن */
    fun addItem(item: OrderItemEntity) {
        item.order = this
        items.add(item)
    }
}

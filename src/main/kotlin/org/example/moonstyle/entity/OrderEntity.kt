package org.example.moonstyle.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "orders")
data class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "user_id", nullable = false)
    val userId: Int,
    
    @Column(name = "total_amount", nullable = false)
    val totalAmount: Long,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
    
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<OrderItemEntity> = mutableListOf()
)
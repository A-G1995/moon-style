package org.example.moonstyle.entity

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "orders", indexes = [Index(name="idx_orders_user", columnList = "user_id")])
data class OrderEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name="user_id", nullable = false)
    val userId: Int,
    
    @Column(nullable = false)
    val total: Long,
    
    @Column(name="created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val items: MutableList<OrderItemEntity> = mutableListOf()
)

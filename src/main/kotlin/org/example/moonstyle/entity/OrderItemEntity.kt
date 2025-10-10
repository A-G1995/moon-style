package org.example.moonstyle.entity

import jakarta.persistence.*

@Entity
@Table(name = "order_items", indexes = [Index(name="idx_order_items_order", columnList = "order_id")])
data class OrderItemEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", nullable = false)
    val order: OrderEntity,
    
    // Snapshot از محصول در لحظه خرید
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id", nullable = false)
    val product: ProductEntity,
    
    @Column(nullable = false) val title: String,
    @Column(nullable = false) val price: Long,
    @Column(nullable = false) val quantity: Int
)

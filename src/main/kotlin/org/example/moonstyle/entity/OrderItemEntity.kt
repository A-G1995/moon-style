package org.example.moonstyle.entity

import jakarta.persistence.*

@Entity
@Table(name = "order_items")
data class OrderItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: OrderEntity,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductEntity,
    
    @Column(nullable = false)
    val title: String,
    @Column(nullable = false)
    val price: Long,
    @Column(nullable = false)
    val quantity: Int,
    @Column(nullable = false)
    val subtotal: Long
)
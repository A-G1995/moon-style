package org.example.moonstyle.entity

import jakarta.persistence.*

@Entity
@Table(name = "order_items")
data class OrderItemEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    var order: OrderEntity? = null,          // ⬅️ var باشد تا قبل از ذخیره ست شود
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductEntity,               // نگه داشتن رفرانس محصول (برای سازگاری)
    
    @Column(nullable = false)
    val title: String,
    
    @Column(nullable = false)
    val price: Long,
    
    @Column(nullable = false)
    var quantity: Int
)

package org.example.moonstyle.entity

import jakarta.persistence.*

@Entity
@Table(name = "cart_items",
    uniqueConstraints = [UniqueConstraint(columnNames = ["cart_id","product_id"])]
)
data class CartItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    val cart: CartEntity,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductEntity,
    
    @Column(nullable = false)
    var quantity: Int = 1
)
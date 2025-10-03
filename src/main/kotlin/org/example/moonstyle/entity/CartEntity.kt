package org.example.moonstyle.entity

import jakarta.persistence.*


@Entity
@Table(name = "carts")
data class CartEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "user_id", nullable = false)
    val userId: Int,
    
    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val items: MutableList<CartItemEntity> = mutableListOf()
)
package org.example.moonstyle.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.util.Date

@Entity
@Table(
    name = "products",
    indexes = [
        Index(name = "idx_products_active_cat_color_size_price", columnList = "isActive,category,color,size,price"),
        Index(name = "idx_products_price", columnList = "price")
    ]
)
data class ProductEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    
    @Column(nullable = false)
    val title: String,
    
    @Column(columnDefinition = "TEXT")
    val description: String? = null,
    
    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,
    
    val imageUrl: String? = null,
    
    @Column(nullable = false)
    val color: String,
    
    @Column(nullable = false)
    val size: String,           // keep simple: "S","M","L","XL"
    
    @Column(nullable = false)
    val category: String,       // e.g., "TSHIRT","HOODIE","JEANS"
    
    @Column(nullable = false)
    val stockQty: Int,
    
    @Column(nullable = false)
    val isActive: Boolean = true,
    
    @Column(name = "created_at", nullable = false)
    var createdAt: Date = Date(),
    
    @Column(nullable = false)
    val updatedAt: Date = Date()

)
package org.example.moonstyle.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    name = "products",
    indexes = [
        Index(
            name = "idx_products_active_cat_color_size_price",
            columnList = "is_active,category,color,size,price"
        ),
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
    
    @Column(nullable = false)             // قیمت به تومان (بدون اعشار)
    val price: Long,
    
    @Column(name = "image_url")
    val imageUrl: String? = null,
    
    @Column(nullable = false)
    val color: String,
    
    @Column(nullable = false)
    val size: String,
    
    @Column(nullable = false)
    val category: String,
    
    @Column(name = "stock_qty", nullable = false)
    val stockQty: Int,
    
    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,
    
    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    @PrePersist
    fun onCreate() {
        val now = Instant.now()
        createdAt = now
        updatedAt = now
    }
    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}

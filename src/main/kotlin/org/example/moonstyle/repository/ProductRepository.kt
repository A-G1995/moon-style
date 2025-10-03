package org.example.moonstyle.repository

import org.example.moonstyle.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<ProductEntity, Int> {
    
    @Query("""
        SELECT p FROM ProductEntity p
        WHERE p.isActive = true
          AND (:q IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%')))
          AND (:color IS NULL OR p.color = :color)
          AND (:size IS NULL OR p.size = :size)
          AND (:category IS NULL OR p.category = :category)
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
    """)
    fun search(
        @Param("q") q: String?,
        @Param("color") color: String?,
        @Param("size") size: String?,
        @Param("category") category: String?,
        @Param("minPrice") minPrice: Long?,
        @Param("maxPrice") maxPrice: Long?
    ): List<ProductEntity>
    
    @Query("""
        SELECT p FROM ProductEntity p
        WHERE p.isActive = true AND p.price BETWEEN :minAmt AND :maxAmt
        ORDER BY p.price ASC
    """)
    fun findByPriceRange(
        @Param("minAmt") minAmt: Long,
        @Param("maxAmt") maxAmt: Long
    ): List<ProductEntity>
}

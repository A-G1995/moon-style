package org.example.moonstyle.repository

import org.example.moonstyle.entity.ProductEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal

interface ProductRepository : JpaRepository<ProductEntity, Int> {
    
    @Query(
        """
        SELECT p FROM ProductEntity p
        WHERE p.isActive = true
          AND p.stockQty > 0
          AND (:q IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%')))
          AND (:color IS NULL OR p.color = :color)
          AND (:size IS NULL OR p.size = :size)
          AND (:category IS NULL OR p.category = :category)
          AND (:priceMin IS NULL OR p.price >= :priceMin)
          AND (:priceMax IS NULL OR p.price <= :priceMax)
        """
    )
    fun search(
        @Param("q") q: String?,
        @Param("color") color: String?,
        @Param("size") size: String?,
        @Param("category") category: String?,
        @Param("priceMin") priceMin: BigDecimal?,
        @Param("priceMax") priceMax: BigDecimal?,
        pageable: Pageable
    ): Page<ProductEntity>
}
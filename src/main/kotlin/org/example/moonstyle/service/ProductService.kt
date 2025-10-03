package org.example.moonstyle.service

import org.example.moonstyle.entity.dto.ProductDto

interface ProductService {
    fun list(
        q: String?,
        color: String?,
        size: String?,
        category: String?,
        priceMin: Long?,
        priceMax: Long?
    ): List<ProductDto>
    
    fun get(id: Int): ProductDto
}
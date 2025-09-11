package org.example.moonstyle.service

import org.example.moonstyle.entity.dto.PageResponse
import org.example.moonstyle.entity.dto.ProductFilter
import org.example.moonstyle.entity.dto.ProductResponse

interface ProductService {
    fun list(filter: ProductFilter): PageResponse<ProductResponse>
    fun get(id: Int): ProductResponse
}
package org.example.moonstyle.entity.dto

import org.example.moonstyle.entity.ProductEntity

data class ProductDto(
    val id: Int,
    val title: String,
    val price: Long,
    val imageUrl: String?,
    val color: String,
    val size: String,
    val category: String,
    val stockQty: Int
)
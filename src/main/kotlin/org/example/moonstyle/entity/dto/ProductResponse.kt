package org.example.moonstyle.entity.dto

import java.math.BigDecimal

data class ProductResponse(
    val id: Int,
    val title: String,
    val price: BigDecimal,
    val color: String,
    val size: String,
    val category: String,
    val imageUrl: String?,
    val stockQty: Int
)
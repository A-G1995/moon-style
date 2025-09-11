package org.example.moonstyle.entity.dto


import java.math.BigDecimal

data class ProductFilter(
    val q: String? = null,
    val color: String? = null,
    val size: String? = null,
    val category: String? = null,
    val priceMin: BigDecimal? = null,
    val priceMax: BigDecimal? = null,
    val page: Int = 0,
    val sizePage: Int = 20,
    val sort: String? = null // e.g. "price,asc" or "price,desc"
)
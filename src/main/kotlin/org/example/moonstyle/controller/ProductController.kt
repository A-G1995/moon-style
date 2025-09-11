package org.example.moonstyle.controller

import org.example.moonstyle.entity.dto.PageResponse
import org.example.moonstyle.entity.dto.ProductFilter
import org.example.moonstyle.entity.dto.ProductResponse
import org.example.moonstyle.service.ProductService
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal


@RestController
@RequestMapping("/products")
class ProductController(
    private val service: ProductService
) {
    @GetMapping
    fun list(
        @RequestParam(required = false) q: String?,
        @RequestParam(required = false) color: String?,
        @RequestParam(required = false) size: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) priceMin: BigDecimal?,
        @RequestParam(required = false) priceMax: BigDecimal?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "20") sizePage: Int,
        @RequestParam(required = false) sort: String?
    ): PageResponse<ProductResponse> {
        val filter = ProductFilter(q, color, size, category, priceMin, priceMax, page, sizePage, sort)
        return service.list(filter)
    }
    
    @GetMapping("/{id}")
    fun get(@PathVariable id: Int): ProductResponse = service.get(id)
}
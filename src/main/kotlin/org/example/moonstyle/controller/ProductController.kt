package org.example.moonstyle.controller

import org.example.moonstyle.entity.dto.ProductDto
import org.example.moonstyle.service.ProductService
import org.springframework.web.bind.annotation.*


@RestController
class ProductController(
    private val service: ProductService
) {
    @GetMapping("/products")
    fun list(
        @RequestParam(required = false) q: String?,
        @RequestParam(required = false) color: String?,
        @RequestParam(required = false) size: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false, name = "priceMin") priceMin: Long?,
        @RequestParam(required = false, name = "priceMax") priceMax: Long?
    ): List<ProductDto> = service.list(q, color, size, category, priceMin, priceMax)
    
    @GetMapping("/products/{id}")
    fun one(@PathVariable id: Int): ProductDto = service.get(id)
}
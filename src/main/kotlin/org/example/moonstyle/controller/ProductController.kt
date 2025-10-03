package org.example.moonstyle.controller

import org.example.moonstyle.repository.ProductRepository
import org.springframework.web.bind.annotation.*


@RestController
class ProductController(
    private val repo: ProductRepository
) {
    @GetMapping("/products")
    fun list(
        @RequestParam(required = false) q: String?,
        @RequestParam(required = false) color: String?,
        @RequestParam(required = false) size: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) priceMin: Long?,
        @RequestParam(required = false) priceMax: Long?
    ) = repo.search(q, color, size, category, priceMin, priceMax)
    
    @GetMapping("/products/{id}")
    fun one(@PathVariable id: Int) =
        repo.findById(id).orElseThrow { RuntimeException("محصول یافت نشد") }
}
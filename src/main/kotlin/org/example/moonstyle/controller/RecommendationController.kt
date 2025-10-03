package org.example.moonstyle.controller

import org.example.moonstyle.entity.ProductEntity
import org.example.moonstyle.service.RecommendationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/recommendations")
class RecommendationController(
    private val service: RecommendationService
) {
    @GetMapping("/spend")
    fun bySpend(
        @RequestParam amount: Long,
        @RequestParam(defaultValue = "20") percent: Int,
        @RequestParam(defaultValue = "8") limit: Int
    ): List<ProductEntity> = service.bySpend(amount, percent, limit)
}
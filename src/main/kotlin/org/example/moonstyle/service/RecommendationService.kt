package org.example.moonstyle.service

import org.example.moonstyle.entity.ProductEntity
import org.example.moonstyle.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class RecommendationService(
    private val repo: ProductRepository
) {
    // amount: مبلغ خرید کاربر (تومان)
    // percent: +- درصد بازه، مثلا 20 یعنی از 80% تا 120% مبلغ خرید
    // limit: سقف تعداد نتایج
    fun bySpend(amount: Long, percent: Int, limit: Int): List<ProductEntity> {
        val pct = percent.coerceIn(5, 80)
        val delta = (amount * pct) / 100L
        val minAmt = (amount - delta).coerceAtLeast(0L)
        val maxAmt = amount + delta
        
        val all = repo.findByPriceRange(minAmt, maxAmt)
        return if (all.size <= limit) all else all.shuffled().take(limit)
    }
}
package org.example.moonstyle.repository

import org.example.moonstyle.entity.OrderItemEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.stereotype.Repository as StRepository

/**
 * DTO برای خروجی نمودار پای: برچسب = دسته‌بندی محصول، مقدار = مجموع تعداد خرید
 */
data class SalesAggRow(
    val category: String,
    val qty: Long
)

/**
 * ریپازیتوری آمار ادمین.
 * نکته: اینجا از Spring Data Repository استفاده شده و فقط متد‌های موردنیاز تعریف شده‌اند.
 * اگر ترجیح می‌دهی از JpaRepository استفاده کنی، می‌توانی به شکل
 * interface AdminStatsRepository : JpaRepository<OrderItemEntity, Long>
 * تغییر بدهی.
 */
@StRepository
interface AdminStatsRepository : Repository<OrderItemEntity, Long> {
    
    /**
     * تجمیع تعداد خرید به تفکیک دسته‌بندی محصول (JPQL)
     * به کلاس OrderItemEntity و رابطه‌ی product آن نیاز دارد.
     */
    @Query("""
    SELECT new org.example.moonstyle.repository.SalesAggRow(p.category, SUM(oi.quantity))
    FROM OrderItemEntity oi
    JOIN oi.product p
    GROUP BY p.category
    ORDER BY SUM(oi.quantity) DESC
""")
    fun salesByCategory(): List<SalesAggRow>
    
}
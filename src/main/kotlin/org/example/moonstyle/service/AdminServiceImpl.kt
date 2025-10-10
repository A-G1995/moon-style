package org.example.moonstyle.service

import org.example.moonstyle.entity.dto.PieChartDto
import org.springframework.transaction.annotation.Transactional
import org.example.moonstyle.repository.AdminStatsRepository
import org.springframework.stereotype.Service

@Service
class AdminServiceImpl(
    private val statsRepo: AdminStatsRepository
) : AdminService {
    
    @Transactional(readOnly = true)
    override fun salesByCategory(): PieChartDto {
        val rows = statsRepo.salesByCategory()
        return PieChartDto(
            labels = rows.map { it.category.ifBlank { "نامشخص" } },
            values = rows.map { it.qty }
        )
    }
}
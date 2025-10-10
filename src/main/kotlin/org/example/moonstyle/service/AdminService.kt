package org.example.moonstyle.service

import org.example.moonstyle.entity.dto.PieChartDto

interface AdminService {
    fun salesByCategory(): PieChartDto
}
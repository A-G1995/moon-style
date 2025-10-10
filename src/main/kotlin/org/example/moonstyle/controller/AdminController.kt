package org.example.moonstyle.controller

import org.example.moonstyle.entity.dto.PieChartDto
import org.example.moonstyle.service.AdminService
import org.example.moonstyle.session.SessionStore
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/admin")
class AdminController(
    private val sessions: SessionStore,
    private val adminService: AdminService
) {
    private fun requireAdmin(sid: String?) {
        val s = sessions.get(sid) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "نیاز به ورود دارید")
        if (!s.isAdmin) throw ResponseStatusException(HttpStatus.FORBIDDEN, "دسترسی غیرمجاز")
    }
    
    @GetMapping("/stats/sales-by-category")
    fun salesByCategory(@RequestHeader("X-Session-Id") sid: String?): PieChartDto {
        requireAdmin(sid)
        return adminService.salesByCategory()
    }
}
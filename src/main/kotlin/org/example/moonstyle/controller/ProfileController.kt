package org.example.moonstyle.controller

import jakarta.validation.Valid
import org.example.moonstyle.entity.dto.ProfileResponse
import org.example.moonstyle.entity.dto.ProfileUpdateRequest
import org.example.moonstyle.service.ProfileService
import org.example.moonstyle.session.SessionStore
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/account")
class ProfileController(
    private val sessions: SessionStore,
    private val service: ProfileService
) {
    private fun userId(sid: String?): Int {
        val s = sessions.get(sid) ?: throw org.springframework.web.server.ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "ابتدا وارد شوید"
        )
        return s.userId?: throw Exception("no user id found")
    }
    
    @GetMapping("/me")
    fun me(@RequestHeader("X-Session-Id") sid: String?): ProfileResponse =
        service.me(userId(sid))
    
    @PutMapping("/me")
    fun update(
        @RequestHeader("X-Session-Id") sid: String?,
        @Valid @RequestBody req: ProfileUpdateRequest
    ): ProfileResponse = service.update(userId(sid), req)
}
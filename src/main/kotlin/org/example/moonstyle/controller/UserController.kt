package org.example.moonstyle.controller

import org.example.moonstyle.entity.dto.AuthResponse
import org.example.moonstyle.entity.dto.LoginRequest
import org.example.moonstyle.entity.dto.SignupRequest
import org.example.moonstyle.entity.dto.UpdateProfileRequest
import org.example.moonstyle.entity.dto.UserProfileDto
import org.example.moonstyle.service.UserService
import org.example.moonstyle.session.SessionStore
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
    private val sessions: SessionStore
) {
    
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(@RequestBody req: SignupRequest): AuthResponse =
        userService.signup(req)
    
    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): AuthResponse =
        userService.login(req)
    
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(@RequestHeader("X-Session-Id") sessionId: String) {
        userService.logout(sessionId)
    }
    
    @GetMapping("/me")
    fun me(@RequestHeader("X-Session-Id") sid: String?): UserProfileDto =
        userService.getProfile(requireUserId(sid))
    
    @PutMapping("/me")
    fun updateMe(
        @RequestHeader("X-Session-Id") sid: String?,
        @RequestBody req: UpdateProfileRequest
    ): UserProfileDto =
        userService.updateProfile(requireUserId(sid), req)
    
    private fun requireUserId(sessionId: String?): Int {
        val s = sessions.get(sessionId)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "نیاز به ورود دارید.")
        return s.userId
    }
}

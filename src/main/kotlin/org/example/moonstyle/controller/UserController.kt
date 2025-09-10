package org.example.moonstyle.controller

import jakarta.validation.Valid
import org.example.moonstyle.entity.dto.AuthResponse
import org.example.moonstyle.entity.dto.LoginRequest
import org.example.moonstyle.entity.dto.SignupRequest
import org.example.moonstyle.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(@Valid @RequestBody req: SignupRequest): AuthResponse =
        userService.signup(req)
    
    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequest): AuthResponse =
        userService.login(req)
    
    
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(@RequestHeader("X-Session-Id", required = true) sessionId: String) {
        userService.logout(sessionId)
    }
}
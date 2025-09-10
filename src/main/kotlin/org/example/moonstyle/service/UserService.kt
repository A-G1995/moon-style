package org.example.moonstyle.service

import org.example.moonstyle.entity.dto.AuthResponse
import org.example.moonstyle.entity.dto.LoginRequest
import org.example.moonstyle.entity.dto.SignupRequest

interface UserService {
    fun signup(req: SignupRequest): AuthResponse
    fun login(req: LoginRequest): AuthResponse
    fun logout(sessionId: String?)
}
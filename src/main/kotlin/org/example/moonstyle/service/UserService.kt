package org.example.moonstyle.service

import org.example.moonstyle.entity.dto.AuthResponse
import org.example.moonstyle.entity.dto.LoginRequest
import org.example.moonstyle.entity.dto.SignupRequest
import org.example.moonstyle.entity.dto.UpdateProfileRequest
import org.example.moonstyle.entity.dto.UserProfileDto

interface UserService {
    fun signup(req: SignupRequest): AuthResponse
    fun login(req: LoginRequest): AuthResponse
    fun logout(sessionId: String?)
    fun getProfile(userId: Int): UserProfileDto
    fun updateProfile(userId: Int, req: UpdateProfileRequest): UserProfileDto
}
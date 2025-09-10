package org.example.moonstyle.entity.dto

data class AuthResponse(
    val userId: Int?,
    val sessionId: String,
    val isAdmin: Boolean
)
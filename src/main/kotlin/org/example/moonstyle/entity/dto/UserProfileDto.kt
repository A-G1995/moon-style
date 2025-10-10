package org.example.moonstyle.entity.dto

data class UserProfileDto(
    val id: Int,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val nationalNumber: String,
    val birthDate: String?
)
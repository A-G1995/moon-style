package org.example.moonstyle.entity.dto

data class UpdateProfileRequest(
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val nationalNumber: String,
    val birthDate: String?
)
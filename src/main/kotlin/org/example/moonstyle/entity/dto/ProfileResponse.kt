package org.example.moonstyle.entity.dto

data class ProfileResponse(
    val id: Int,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val nationalNumber: String,
    val birthDate: String? = null, // yyyy-MM-dd or null
    val isAdmin: Boolean
)
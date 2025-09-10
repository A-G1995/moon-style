package org.example.moonstyle.entity.dto


import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignupRequest(
    @field:Email @field:NotBlank val email: String,
    @field:NotBlank @field:Size(min = 3, max = 50) val fullName: String,
    @field:NotBlank @field:Size(min = 4, max = 100) val password: String,
    @field:NotBlank val phoneNumber: String,
    @field:NotBlank val nationalNumber: String
)
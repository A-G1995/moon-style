package org.example.moonstyle.entity.dto


import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.example.moonstyle.validation.DigitsOnly

data class SignupRequest(
    @field:Email(message = "ایمیل معتبر نیست")
    @field:NotBlank(message = "ایمیل الزامی است")
    val email: String,
    
    @field:NotBlank(message = "نام کامل الزامی است")
    val fullName: String,
    
    @field:NotBlank(message = "رمز عبور الزامی است")
    val password: String,
    
    @field:NotBlank(message = "شماره تلفن الزامی است")
    @field:Pattern(
        regexp = "^09\\d{9}$",
        message = "شماره تلفن باید با 09 شروع شود و دقیقاً 11 رقم باشد"
    )
    val phoneNumber: String,
    
    @field:NotBlank(message = "کد ملی الزامی است")
    @field:DigitsOnly(message = "کد ملی باید فقط شامل رقم باشد")
    @field:Size(min = 10, max = 10, message = "کد ملی باید دقیقاً 10 رقم باشد")
    val nationalNumber: String
)
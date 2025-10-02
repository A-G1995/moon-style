package org.example.moonstyle.entity.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.example.moonstyle.validation.DigitsOnly

data class ProfileUpdateRequest(
    @field:Email(message = "ایمیل معتبر نیست")
    @field:NotBlank(message = "ایمیل الزامی است")
    val email: String,
    
    @field:NotBlank(message = "نام کامل الزامی است")
    val fullName: String,
    
    @field:NotBlank(message = "شماره تلفن الزامی است")
    @field:DigitsOnly(message = "شماره تلفن باید فقط شامل رقم باشد")
    @field:Pattern(regexp = "^09\\d{9}$", message = "شماره تلفن باید با 09 شروع شود و دقیقاً 11 رقم باشد")
    val phoneNumber: String,
    
    @field:NotBlank(message = "کد ملی الزامی است")
    @field:DigitsOnly(message = "کد ملی باید فقط شامل رقم باشد")
    @field:Size(min = 10, max = 10, message = "کد ملی باید دقیقاً 10 رقم باشد")
    val nationalNumber: String,
    
    // اختیاری. اگر می‌خواهی تاریخ را الزامی کنی، @NotBlank بگذار.
    @field:Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}$",
        message = "تاریخ تولد باید به صورت yyyy-MM-dd باشد"
    )
    val birthDate: String? = null
)
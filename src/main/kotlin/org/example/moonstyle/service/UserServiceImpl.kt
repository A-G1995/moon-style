package org.example.moonstyle.service

import org.example.moonstyle.entity.UserEntity
import org.example.moonstyle.entity.dto.AuthResponse
import org.example.moonstyle.entity.dto.LoginRequest
import org.example.moonstyle.entity.dto.SignupRequest
import org.example.moonstyle.entity.dto.UpdateProfileRequest
import org.example.moonstyle.entity.dto.UserProfileDto
import org.example.moonstyle.repository.UserRepository
import org.example.moonstyle.session.SessionStore
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class UserServiceImpl(
    private val userRepo: UserRepository,
    private val sessions: SessionStore
) : UserService {
    
    @Transactional
    override fun signup(req: SignupRequest): AuthResponse {
        // چک ساده ایمیل (برای ارور واضح‌تر)
        if (userRepo.findByEmail(req.email.trim()) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "کاربری با این ایمیل وجود دارد.")
        }
        
        val entity = UserEntity(
            id = null,
            email = req.email.trim(),
            password = req.password.trim(),      // طبق خواسته: بدون هش
            fullName = req.fullName.trim(),
            phoneNumber = req.phoneNumber.trim(),
            nationalNumber = req.nationalNumber.trim(),
            isAdmin = false,
            // createdAt در Entity مقداردهی پیش‌فرض دارد
        )
        
        val saved = try {
            userRepo.save(entity)
        } catch (ex: DataIntegrityViolationException) {
            // اگر یونیک کدملی/ایمیل بترکه
            throw ResponseStatusException(HttpStatus.CONFLICT, "ایمیل یا کد ملی تکراری است.")
        }
        
        val sid = sessions.create(saved.id!!, saved.isAdmin)
        return AuthResponse(
            sessionId = sid,
            userId = saved.id!!,
            isAdmin = saved.isAdmin,
            email = saved.email,
            fullName = saved.fullName
        )
    }
    
    @Transactional(readOnly = true)
    override fun login(req: LoginRequest): AuthResponse {
        // چون ریپازیتوری‌ات این متد را دارد، مستقیم استفاده می‌کنیم
        val user = userRepo.findByEmailAndPassword(
            req.email.trim(),
            req.password.trim()
        ) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "ایمیل/رمز نادرست")
        
        val sid = sessions.create(user.id!!, user.isAdmin)
        return AuthResponse(
            sessionId = sid,
            userId = user.id!!,
            isAdmin = user.isAdmin,
            email = user.email,
            fullName = user.fullName
        )
    }
    
    @Transactional
    override fun logout(sessionId: String?) {
        sessions.remove(sessionId)
    }
    
    @Transactional(readOnly = true)
    override fun getProfile(userId: Int): UserProfileDto {
        val u = userRepo.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "کاربر یافت نشد.")
        }
        return UserProfileDto(
            id = u.id!!,
            fullName = u.fullName,
            email = u.email,
            phoneNumber = u.phoneNumber,
            nationalNumber = u.nationalNumber,
            birthDate = u.birthDate?.toString()
        )
    }
    
    @Transactional
    override fun updateProfile(userId: Int, req: UpdateProfileRequest): UserProfileDto {
        val u = userRepo.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "کاربر یافت نشد.")
        }
        
        // چون فیلدهای UserEntity همگی val هستند، باید با copy یک نمونهٔ جدید بسازیم
        val newBirth: LocalDate? = req.birthDate?.takeIf { it.isNotBlank() }?.let {
            runCatching { LocalDate.parse(it) }.getOrNull()
        }
        
        val updated = u.copy(
            email = req.email.trim(),
            fullName = req.fullName.trim(),
            phoneNumber = req.phoneNumber.trim(),
            nationalNumber = req.nationalNumber.trim(),
            birthDate = newBirth
        )
        
        val saved = try {
            userRepo.save(updated)
        } catch (ex: DataIntegrityViolationException) {
            // یونیک کانسترینت‌ها (ایمیل/کدملی)
            throw ResponseStatusException(HttpStatus.CONFLICT, "ایمیل یا کد ملی تکراری است.")
        }
        
        return UserProfileDto(
            id = saved.id!!,
            fullName = saved.fullName,
            email = saved.email,
            phoneNumber = saved.phoneNumber,
            nationalNumber = saved.nationalNumber,
            birthDate = saved.birthDate?.toString()
        )
    }
}

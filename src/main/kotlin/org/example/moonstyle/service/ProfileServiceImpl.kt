package org.example.moonstyle.service

import org.example.moonstyle.entity.dto.ProfileResponse
import org.example.moonstyle.entity.dto.ProfileUpdateRequest
import org.example.moonstyle.repository.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class ProfileServiceImpl(
    private val repo: UserRepository
) : ProfileService {
    
    @Transactional(readOnly = true)
    override fun me(userId: Int): ProfileResponse {
        val u = repo.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "کاربر یافت نشد")
        }
        return ProfileResponse(
            id = u.id!!,
            email = u.email,
            fullName = u.fullName,
            phoneNumber = u.phoneNumber,
            nationalNumber = u.nationalNumber,
            birthDate = u.birthDate?.toString(),
            isAdmin = u.isAdmin
        )
    }
    
    @Transactional
    override fun update(userId: Int, req: ProfileUpdateRequest): ProfileResponse {
        val u = repo.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "کاربر یافت نشد")
        }
        
        val cleanPhone = req.phoneNumber
        val cleanNational = req.nationalNumber
        val birth = req.birthDate?.let { LocalDate.parse(it) }
        
        val updated = u.copy(
            email = req.email.trim().lowercase(),
            fullName = req.fullName.trim(),
            phoneNumber = cleanPhone,
            nationalNumber = cleanNational,
            birthDate = birth
            // isAdmin تغییر نمی‌کند
        )
        
        try {
            val saved = repo.save(updated)
            return ProfileResponse(
                id = saved.id!!,
                email = saved.email,
                fullName = saved.fullName,
                phoneNumber = saved.phoneNumber,
                nationalNumber = saved.nationalNumber,
                birthDate = saved.birthDate?.toString(),
                isAdmin = saved.isAdmin
            )
        } catch (ex: DataIntegrityViolationException) {
            // اگر ایمیل یا کدملی یکتا باشد، اینجا خطای فارسی برگردان
            throw ResponseStatusException(HttpStatus.CONFLICT, "ایمیل یا کد ملی تکراری است")
        }
    }
}
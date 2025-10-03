package org.example.moonstyle.service

import org.example.moonstyle.entity.UserEntity
import org.example.moonstyle.entity.dto.AuthResponse
import org.example.moonstyle.entity.dto.LoginRequest
import org.example.moonstyle.entity.dto.SignupRequest
import org.example.moonstyle.repository.UserRepository
import org.example.moonstyle.session.SessionStore
import org.springframework.stereotype.Service
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@Service
class UserServiceImpl(
    private val userRepo: UserRepository,
    private val sessions: SessionStore
) : UserService {
    
    override fun signup(req: SignupRequest): AuthResponse {
        // چک تکراری‌نبودن ایمیل
        if (userRepo.findByEmail(req.email) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "این ایمیل قبلاً ثبت شده است")
        }
        val u = userRepo.save(
            UserEntity(
                email = req.email.trim(),
                password = req.password,
                fullName = req.fullName.trim(),
                phoneNumber = req.phoneNumber.trim(),
                nationalNumber = req.nationalNumber.trim(),
                isAdmin = false
            )
        )
        val sid = sessions.create(u.id!!, u.isAdmin)
        return AuthResponse(
            sessionId = sid,
            userId = u.id!!,
            email = u.email,
            fullName = u.fullName,
            isAdmin = u.isAdmin
        )
    }
    
    override fun login(req: LoginRequest): AuthResponse {
        val u = userRepo.findByEmailAndPassword(req.email.trim(), req.password)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "ایمیل یا رمز عبور نادرست است")
        
        val sid = sessions.create(u.id!!, u.isAdmin)
        return AuthResponse(
            sessionId = sid,
            userId = u.id!!,
            email = u.email,
            fullName = u.fullName,
            isAdmin = u.isAdmin
        )
    }
    
    override fun logout(sessionId: String) {
        sessions.remove(sessionId)
    }
}
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
class UserServiceImp(
    private val repository: UserRepository,
    private val sessionStore: SessionStore
) : UserService {
    override fun signup(req: SignupRequest): AuthResponse {
        val email = req.email.lowercase().trim()
        if (repository.existsByEmail(email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email already registered")
        }
        val user = repository.save(
            UserEntity(
                email = email,
                password = req.password,              // PLAIN TEXT (demo only)
                fullName = req.fullName.trim(),
                phoneNumber = req.phoneNumber,
                nationalNumber = req.nationalNumber.trim()
            )
        )
        val s = sessionStore.create(user.id, user.isAdmin)
        return AuthResponse(userId = user.id, sessionId = s.sessionId, isAdmin = user.isAdmin)
    }
    
    override fun login(req: LoginRequest): AuthResponse {
        val email = req.email.lowercase().trim()
        val user = repository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
        if (req.password != user.password) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
        }
        val s = sessionStore.create(user.id, user.isAdmin)
        return AuthResponse(userId = user.id, sessionId = s.sessionId, isAdmin = user.isAdmin)
    }
    
    override fun logout(sessionId: String?) {
        if (sessionId.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing X-Session-Id")
        }
        sessionStore.delete(sessionId)
    }
    
}
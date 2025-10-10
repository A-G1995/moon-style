package org.example.moonstyle.session

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class SessionGate(private val sessions: SessionStore) {
    fun requireUserId(sessionId: String?): Int {
        val s = sessions.get(sessionId) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "نشست نامعتبر")
        return s.userId
    }
}
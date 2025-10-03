package org.example.moonstyle.session

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class SessionGate(private val sessions: SessionStore) {
    fun requireUserId(sid: String?): Int {
        val s = sessions.get(sid) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "ابتدا وارد شوید")
        return s.userId!!
    }
}
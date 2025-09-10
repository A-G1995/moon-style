package org.example.moonstyle.session

import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class Session(
    val sessionId: String,
    val userId: Int?,
    val isAdmin: Boolean,
    val createdAt: Instant
)

@Component
class SessionStore {
    private val sessions = ConcurrentHashMap<String, Session>()
    
    fun create(userId: Int?, isAdmin: Boolean): Session {
        val sid = UUID.randomUUID().toString()
        val s = Session(sid, userId, isAdmin, Instant.now())
        sessions[sid] = s
        return s
    }
    
    fun get(sessionId: String?): Session? = sessionId?.let { sessions[it] }
    
    fun delete(sessionId: String?) {
        if (sessionId != null) sessions.remove(sessionId)
    }
}
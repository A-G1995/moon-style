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

data class SessionData(val userId: Int, val isAdmin: Boolean)

@Component
class SessionStore {
    private val map = ConcurrentHashMap<String, SessionData>()
    fun create(userId: Int, isAdmin: Boolean): String {
        val sid = UUID.randomUUID().toString()
        map[sid] = SessionData(userId, isAdmin)
        return sid
    }
    fun get(sessionId: String?): SessionData? = if (sessionId == null) null else map[sessionId]
    fun remove(sessionId: String?) { if (sessionId != null) map.remove(sessionId) }
}
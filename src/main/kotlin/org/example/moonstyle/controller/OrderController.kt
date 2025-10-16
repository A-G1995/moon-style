package org.example.moonstyle.web

import org.example.moonstyle.service.OrderService
import org.example.moonstyle.session.SessionStore
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/orders")
class OrderController(
    private val sessions: SessionStore,
    private val orderService: OrderService
) {
    private fun requireUserId(sid: String?): Int =
        sessions.get(sid)?.userId ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "ابتدا وارد شوید")
    
    @PostMapping("/checkout")
    fun checkout(@RequestHeader("X-Session-Id") sid: String?): Any =
        orderService.checkout(requireUserId(sid))
    
    @GetMapping
    fun listMyOrders(@RequestHeader("X-Session-Id") sid: String?): Any =
        orderService.listMyOrders(requireUserId(sid))
    
    @GetMapping("/{id}")
    fun getOrder(@RequestHeader("X-Session-Id") sid: String?, @PathVariable id: Long): Any =
        orderService.getOrder(requireUserId(sid), id)
}

package org.example.moonstyle.controller

import org.example.moonstyle.service.OrderService
import org.example.moonstyle.session.SessionGate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(
    private val gate: SessionGate,
    private val orderService: OrderService
) {
    @PostMapping("/checkout")
    fun checkout(@RequestHeader("X-Session-Id") sid: String?) =
        orderService.checkout(gate.requireUserId(sid))
}
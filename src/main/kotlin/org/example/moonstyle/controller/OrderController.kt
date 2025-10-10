package org.example.moonstyle.controller

import org.example.moonstyle.entity.dto.OrderDto
import org.example.moonstyle.service.OrderService
import org.example.moonstyle.session.SessionGate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(
    private val gate: SessionGate,
    private val orderService: OrderService
) {
    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.OK)
    fun checkout(@RequestHeader("X-Session-Id") sid: String?): OrderDto =
        orderService.checkout(gate.requireUserId(sid))
    
    @GetMapping
    fun myOrders(@RequestHeader("X-Session-Id") sid: String?): List<OrderDto> =
        orderService.listForUser(gate.requireUserId(sid))
    
    @GetMapping("/{orderId}")
    fun orderDetail(
        @RequestHeader("X-Session-Id") sid: String?,
        @PathVariable orderId: Long
    ): OrderDto =
        orderService.getOne(gate.requireUserId(sid), orderId)
}
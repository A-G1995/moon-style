package org.example.moonstyle.controller

import org.example.moonstyle.entity.dto.CartItemRequest
import org.example.moonstyle.service.CartService
import org.example.moonstyle.session.SessionGate
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cart")
class CartController(
    private val gate: SessionGate,
    private val cartService: CartService
) {
    @GetMapping
    fun getCart(@RequestHeader("X-Session-Id") sid: String?) =
        cartService.getCart(gate.requireUserId(sid))
    
    @PostMapping("/items")
    fun addOrUpdate(
        @RequestHeader("X-Session-Id") sid: String?,
        @RequestBody req: CartItemRequest
    ) = cartService.addOrUpdate(gate.requireUserId(sid), req)
    
    @DeleteMapping("/items/{productId}")
    fun removeItem(
        @RequestHeader("X-Session-Id") sid: String?,
        @PathVariable productId: Long
    ) = cartService.removeItem(gate.requireUserId(sid), productId)
    
    @DeleteMapping
    fun clear(@RequestHeader("X-Session-Id") sid: String?) =
        cartService.clear(gate.requireUserId(sid))
}
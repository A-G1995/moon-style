package org.example.moonstyle.service

import org.example.moonstyle.controller.DTO.UserDto

interface UserService {
    fun create(user: UserDto): UserDto
    fun login(email: String, password: String): UserDto
    fun register(user: UserDto): UserDto
}
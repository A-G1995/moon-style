package org.example.moonstyle.service

import org.example.moonstyle.entity.dto.ProfileResponse
import org.example.moonstyle.entity.dto.ProfileUpdateRequest

interface ProfileService {
    fun me(userId: Int): ProfileResponse
    fun update(userId: Int, req: ProfileUpdateRequest): ProfileResponse
}
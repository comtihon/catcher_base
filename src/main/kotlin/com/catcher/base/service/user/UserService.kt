package com.catcher.base.service.user

import com.catcher.base.data.dto.UserDTO

interface UserService {
    fun registerUser(user: UserDTO): UserDTO

    fun findByEmail(email: String): UserDTO
}
package com.catcher.base.service.user

import com.catcher.base.data.dao.User
import com.catcher.base.data.dto.UserDTO

interface UserService {
    fun registerUser(user: UserDTO): User
}
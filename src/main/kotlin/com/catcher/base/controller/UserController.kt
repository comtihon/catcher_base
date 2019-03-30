package com.catcher.base.controller

import com.catcher.base.data.dto.UserDTO
import com.catcher.base.exception.PasswordRequiredException
import com.catcher.base.service.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/user")
class UserController(@Autowired val userService: UserService) {
    @PostMapping()
    fun new(@Valid @RequestBody user: UserDTO): UserDTO {
        if (user.password.isNullOrEmpty())
            throw PasswordRequiredException("Password required")
        return userService.registerUser(user).toDTO()
    }
}
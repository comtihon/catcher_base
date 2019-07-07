package com.catcher.base.controller

import com.catcher.base.data.dto.UserDTO
import com.catcher.base.exception.PasswordRequiredException
import com.catcher.base.service.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import java.security.Principal
import org.springframework.web.bind.annotation.RequestMapping



@RestController
@RequestMapping("/api/v1/user")
class UserController(@Autowired val userService: UserService) {
    @PostMapping
    fun new(@Valid @RequestBody user: UserDTO): UserDTO {
        if (user.password.isNullOrEmpty())
            throw PasswordRequiredException("Password required")
        return userService.registerUser(user).toDTO()
    }

    @GetMapping
    fun user(principal: Principal): Principal {
        // TODO principal to UserDTO, remove password?
        return principal
    }
}
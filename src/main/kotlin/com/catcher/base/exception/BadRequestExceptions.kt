package com.catcher.base.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class EmailExistsException(msg: String) : RuntimeException(msg)

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class PasswordRequiredException(msg: String) : RuntimeException(msg)

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class RoleIdRequiredException(msg: String) : RuntimeException(msg) {
    constructor() : this("Role.id required")
}

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class BadAuthException(msg: String) : RuntimeException(msg) {
    constructor() : this("Invalid username/password supplied")
}
package com.catcher.base.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class EmailExistsException(msg: String) : BusinessException(msg)

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class PasswordRequiredException(msg: String) : BusinessException(msg)

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class RoleIdRequiredException(msg: String) : BusinessException(msg) {
    constructor() : this("Role.id required")
}

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class NoTestContentException(msg: String) : BusinessException(msg) {
    constructor() : this("No content was provided")
}

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class FileException(msg: String) : BusinessException(msg)

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class ParamRequiredException(msg: String): BusinessException(msg)
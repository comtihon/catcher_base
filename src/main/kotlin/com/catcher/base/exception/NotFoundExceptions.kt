package com.catcher.base.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class TeamNotFoundException(msg: String) : RuntimeException(msg) {
    constructor() : this("No such team")
}

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class UserNotFoundException(msg: String) : RuntimeException(msg) {
    constructor() : this("No such user")
}

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class ProjectNotFoundException(msg: String) : RuntimeException(msg) {
    constructor() : this("No such project")
}

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class RoleNotFoundException(msg: String) : RuntimeException(msg) {
    constructor() : this("No such role")
}
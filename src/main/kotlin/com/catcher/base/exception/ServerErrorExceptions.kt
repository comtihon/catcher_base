package com.catcher.base.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.io.File

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
class ProjectPathExists(msg: String) : RuntimeException(msg) {
    constructor(path: File) : this("Project path $path already exists")
}
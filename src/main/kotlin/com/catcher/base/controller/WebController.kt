package com.catcher.base.controller

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping


@Controller
class WebController : ErrorController {
    override fun getErrorPath(): String {
        return "/error"
    }

    @RequestMapping(value = ["/error"])
    fun redirectApi(): String {
        return "forward:/index.html"
    }
}


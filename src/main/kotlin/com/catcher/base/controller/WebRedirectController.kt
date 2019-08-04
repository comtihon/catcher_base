package com.catcher.base.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping


@Controller
class WebRedirectController {

    @RequestMapping(value = ["/{path:[^\\.]*}"])
    fun forwardAngularPaths(): String {
        return "forward:/index.html"
    }
}
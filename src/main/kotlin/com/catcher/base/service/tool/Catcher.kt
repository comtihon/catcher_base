package com.catcher.base.service.tool

import com.catcher.base.exception.ExecutionFailedException
import com.catcher.base.service.tool.system.SystemTool
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Catcher(@Autowired val systemTool: SystemTool) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun version(): String {
        return try {
            systemTool.execute("catcher -v")
        } catch (e: ExecutionFailedException) {
            "not installed"
        }
    }

    fun install() {
        systemTool.execute("pip install catcher")
    }

    // TODO list available modules

    // TODO install module

    fun runTest(test: String): String {
        log.debug("Run $test")
        // TODO inventory
        // TODO autowire modules path
        // TODO other useful options (resources, log level, etc).
        return systemTool.execute("catcher $test")
    }
}
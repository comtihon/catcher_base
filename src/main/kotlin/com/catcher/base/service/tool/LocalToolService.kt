package com.catcher.base.service.tool

import com.catcher.base.service.tool.system.SystemTool
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class LocalToolService(@Autowired val systemTool: SystemTool,
                       @Autowired val catcher: Catcher) : ToolService {
    lateinit var version: String
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun init() {
        version = systemTool.version()
        if (version == "not installed") { // no environment installed
            log.info("Installing python. This may take a while")
            systemTool.install()
        }
        if (catcher.version() == "not installed") {
            log.info("Installing catcher. This may take a while")
            catcher.install()
        }
    }

    override fun pythonVersion(): Pair<String, String> {
        val toolVer = systemTool.version().trim()
        val toolName = systemTool::class.simpleName!!
        return Pair(toolName, toolVer)
    }

    override fun catcherVersion(): String {
        return catcher.version().trim()
    }

    override fun updateCatcher(version: String?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
package com.catcher.base.service.tool.system

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import javax.transaction.NotSupportedException

/**
 * This package is not recommended. Please, use conda instead.
 */
@Component
@ConditionalOnProperty(prefix = "catcher.system", name = ["native_executable"])
class Native : SystemTool() {

    @Value("\${catcher.system.native_executable:}")
    private val executable: String? = null
    @Value("\${catcher.system.pip_executable:}")
    private val pipExecutable: String? = null

    override fun install() {
        throw NotSupportedException("Automatic installation is not supported")
    }

    override fun execute(command: String): String {
        return command.replaceExecutable("python", executable!!)
                .replaceExecutable("pip", pipExecutable!!)
                .runCommand()
    }

    /**
     * this = pip install catcher
     * from pip
     * to pip3
     * result = pip3 install catcher
     */
    private fun String.replaceExecutable(from: String, to: String): String {
        return when {
            this.startsWith(from) -> "$to${this.substring(this.indexOf(' '))}"
            else -> this
        }
    }
}
package com.catcher.base.service.tool.system

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix="catcher.system", name = ["venv_name"])
class Venv : SystemTool {
    @Value("\${catcher.system.venv_name:}")
    private val env: String? = null
    @Value("\${catcher.system.venv_executable:python3}")
    private val executable: String? = null

    override fun install() {
        "$executable -m pip install --user virtualenv".runCommand()
        "$executable -m venv $env".runCommand()
    }

    override fun execute(command: String): String {
        return runCommand(arrayListOf("bash", "-c", "source $env/bin/activate; $command"))
    }
}
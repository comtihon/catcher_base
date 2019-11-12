package com.catcher.base.service.tool.system

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "catcher.system", name = ["conda_name"])
class Conda : SystemTool() {

    @Value("\${catcher.system.conda_name:}")
    private val env: String? = null
    @Value("\${catcher.system.conda_python:3.7}")
    private val python: String? = null

    override fun install() {
        // TODO determine target platform and try to install conda https://docs.conda.io/en/latest/miniconda.html
        "conda create -n $env --copy -y python=$python".runCommand()
    }

    override fun execute(command: String): String {
        return runCommand(arrayListOf("bash", "-c", "source activate $env && $command"))
    }
}
package com.catcher.base.service.tool.system

import com.catcher.base.exception.ExecutionFailedException
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

abstract class SystemTool {
    private val log = LoggerFactory.getLogger(this::class.java)

    open fun version(): String {
        return execute("python --version")
    }

    @Throws(ExecutionFailedException::class)
    abstract fun install()   // TODO when install should be run? (via UI manually/on start automatically)

    @Throws(ExecutionFailedException::class)
    abstract fun execute(command: String): String

    @Throws(ExecutionFailedException::class)
    fun String.runCommand(workingDir: File = File(".")): String {
        return runCommand(this.split("\\s".toRegex()), workingDir)
    }

    @Throws(ExecutionFailedException::class)
    fun runCommand(command: List<String>, workingDir: File = File(".")): String {
        var proc: Process? = null
        try {
            proc = ProcessBuilder(command)
                    .directory(workingDir)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()
            proc.waitFor()
            if (proc.exitValue() != 0)
                throw ExecutionFailedException(proc!!.errorStream.bufferedReader().readText())
            if (proc.errorStream.available() != 0) {
                log.warn(proc.errorStream.bufferedReader().readText())
            }
            return proc.inputStream.bufferedReader().readText()
        } catch (e: IOException) {
            throw ExecutionFailedException(proc!!.errorStream.bufferedReader().readText())
        }
    }
}
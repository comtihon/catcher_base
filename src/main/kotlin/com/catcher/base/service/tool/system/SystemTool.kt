package com.catcher.base.service.tool.system

import com.catcher.base.exception.ExecutionFailedException
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

abstract class SystemTool {
    private val log = LoggerFactory.getLogger(this::class.java)

    open fun version(): String {
        return try {
            execute("python --version")  // TODO check python3 as well and save executable
        } catch (e: ExecutionFailedException) {
            "not installed"
        }
    }

    @Throws(ExecutionFailedException::class)
    abstract fun install()   // TODO when install should be run? (via UI manually/on start automatically)

    @Throws(ExecutionFailedException::class)
    abstract fun execute(command: String): String

    @Throws(ExecutionFailedException::class)
    fun String.runCommand(workingDir: File = File(".")): String {
        log.info("run: $this")
        return runCommand(this.split("\\s".toRegex()), workingDir)
    }

    @Throws(ExecutionFailedException::class)
    fun runCommand(command: List<String>, workingDir: File = File(".")): String {
        var proc: Process?
        try {
            proc = ProcessBuilder(command)
                    .directory(workingDir)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()
            proc.waitFor()
            if (proc.errorStream.available() != 0) {
                log.warn(proc.errorStream.bufferedReader().readText())
            }
            if (proc.exitValue() != 0) {
                var errorMsg = proc!!.errorStream.bufferedReader().readText()
                if (errorMsg.isBlank())
                    errorMsg = proc.inputStream.bufferedReader().readText()
                throw ExecutionFailedException(errorMsg)
            }
            return proc.inputStream.bufferedReader().readText()
        } catch (e: IOException) {
            throw ExecutionFailedException(e.message!!)
        }
    }
}
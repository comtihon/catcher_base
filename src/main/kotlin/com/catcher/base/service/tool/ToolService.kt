package com.catcher.base.service.tool

interface ToolService {
    fun pythonVersion(): Pair<String, String>
    fun catcherVersion(): String
    fun updateCatcher(version: String?): String
}
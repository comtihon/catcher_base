package com.catcher.base.service.tool

interface ToolService {
    fun pythonVersion(): String
    fun catcherVersion(): String
    fun updateCatcher(version: String?): String
}
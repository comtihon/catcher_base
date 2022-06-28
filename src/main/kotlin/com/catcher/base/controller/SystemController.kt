package com.catcher.base.controller

import com.catcher.base.data.dto.SystemInfoDTO
import com.catcher.base.service.tool.ToolService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/system")
class SystemController(@Autowired val toolService: ToolService) {

    @Autowired
    private val buildProperties: BuildProperties? = null

    @GetMapping
    fun getInfo(): SystemInfoDTO {
        val python = toolService.pythonVersion()
        return SystemInfoDTO(
                pythonProvider = python.first,
                pythonVersion = python.second,
                catcher = toolService.catcherVersion(),
                version = buildProperties!!.version)
    }

    @PutMapping("/catcher/{id}")
    fun updateCatcher(@PathVariable id: String?): String {  // TODO return Future!
        return toolService.updateCatcher(id)
    }
}
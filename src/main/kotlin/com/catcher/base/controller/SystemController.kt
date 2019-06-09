package com.catcher.base.controller

import com.catcher.base.data.dto.SystemInfoDTO
import com.catcher.base.service.tool.ToolService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/system")
class SystemController(@Autowired val toolService: ToolService) {

    @Value("\${server.version:not available}")
    private val version: String? = null

    @GetMapping
    fun getInfo(): SystemInfoDTO {
        return SystemInfoDTO(
                python = toolService.pythonVersion(),
                catcher = toolService.catcherVersion(),
                version = version!!)
    }

    @PutMapping("/catcher/{id}")
    fun updateCatcher(@PathVariable id: String?): String {  // TODO return Future!
        return toolService.updateCatcher(id)
    }
}
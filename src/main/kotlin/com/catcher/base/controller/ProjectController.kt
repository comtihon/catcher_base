package com.catcher.base.controller

import com.catcher.base.data.dto.ProjectDTO
import com.catcher.base.data.dto.TeamDTO
import com.catcher.base.service.project.ProjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/project")
class ProjectController(@Autowired val projectService: ProjectService) {

    @PostMapping
    fun new(@RequestBody project: ProjectDTO): ProjectDTO {
        return projectService.newProject(project)
    }

    @GetMapping
    fun listAll(): List<ProjectDTO> {
        return projectService.getAll()
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Int): ProjectDTO { // TODO async query?
        // TODO test projects.teams.projects recursion
        return projectService.findById(id)
    }

    @PutMapping("/{id}/add_team")
    fun addTeam(@PathVariable id: Int, @Valid @RequestBody team: TeamDTO) {
        projectService.addTeamToProject(id, team)
    }

    @PutMapping("/{id}/del_team")
    fun delTeam(@PathVariable id: Int, @Valid @RequestBody team: TeamDTO) {
        projectService.removeTeamFromProject(id, team)
    }

    @DeleteMapping("/{id}")
    fun delProject(@PathVariable id: Int) {
        projectService.delProject(id)
    }
}
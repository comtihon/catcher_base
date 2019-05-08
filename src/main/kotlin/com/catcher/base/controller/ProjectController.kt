package com.catcher.base.controller

import com.catcher.base.data.dto.ProjectDTO
import com.catcher.base.data.dto.TeamDTO
import com.catcher.base.data.dto.TestDTO
import com.catcher.base.service.project.ProjectService
import com.catcher.base.service.test.TestService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/project")
class ProjectController(@Autowired val projectService: ProjectService,
                        @Autowired val testService: TestService) {

    @PostMapping
    fun new(@RequestBody project: ProjectDTO): ProjectDTO {
        return projectService.newProject(project)
    }

    @GetMapping
    fun listAll(): List<ProjectDTO> {
        return projectService.getAllForUser()
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

    @PostMapping("/{id}/add_test")
    fun createTest(@PathVariable id: Int, @Valid @RequestBody test: TestDTO) {
        testService.newTest(id, test)
    }

    @DeleteMapping("/{project_id}/del_test/{id}")
    fun delete(@PathVariable project_id: Int, @PathVariable id: Int) {
        testService.deleteTest(project_id, id)
    }

    @DeleteMapping("/{id}")
    fun delProject(@PathVariable id: Int) {
        projectService.delProject(id)
    }
}
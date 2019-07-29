package com.catcher.base.controller

import com.catcher.base.data.dto.TeamDTO
import com.catcher.base.data.dto.UserDTO
import com.catcher.base.exception.TeamNotFoundException
import com.catcher.base.service.team.TeamService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/team")
class TeamController(@Autowired val teamService: TeamService) {

    @PostMapping
    fun new(@Valid @RequestBody team: TeamDTO): TeamDTO {
        return teamService.upsertTeam(team)
    }

    @GetMapping
    fun listAll(): List<TeamDTO> {
        return teamService.getAll()
    }

    // TODO request team membership from a normal user

    @GetMapping("/my")
    fun my(): List<TeamDTO> {
        return teamService.getAllForUser()
    }

    @GetMapping("/{name}")
    fun get(@PathVariable name: String): TeamDTO {
        return teamService.findByName(name) ?: throw TeamNotFoundException("No such team %s".format(name))
    }

    @PutMapping("/{name}/add_user")
    fun addUser(@PathVariable name: String, @Valid @RequestBody user: UserDTO) {
        teamService.addUserToTeam(name, user.email)
    }

    @PutMapping("/{name}/del_user")
    fun delUser(@PathVariable name: String, @Valid @RequestBody user: UserDTO) {
        teamService.removeUserFromTeam(name, user)
    }

    @DeleteMapping("/{name}")
    fun delTeam(@PathVariable name: String) {
        teamService.deleteTeam(name)
    }
}
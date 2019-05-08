package com.catcher.base.service.team

import com.catcher.base.data.dto.TeamDTO
import com.catcher.base.data.dto.UserDTO

interface TeamService {
    fun newTeam(team: TeamDTO): TeamDTO

    /**
     * Add user to a team.
     * @param name - team name
     * @param userDto - user dto object. Only email is used.
     */
    fun addUserToTeam(name: String, userDto: UserDTO)

    fun removeUserFromTeam(name: String, userDto: UserDTO)

    fun deleteTeam(name: String)

    fun getAll(): List<TeamDTO>

    fun getAllForUser(): List<TeamDTO>

    fun findByName(name: String): TeamDTO?
}
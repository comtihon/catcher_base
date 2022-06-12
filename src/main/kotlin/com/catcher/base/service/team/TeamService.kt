package com.catcher.base.service.team

import com.catcher.base.data.entity.Team
import com.catcher.base.data.dto.TeamDTO
import com.catcher.base.data.dto.UserDTO

interface TeamService {
    /**
     * save or update team. Return DTO
     */
    fun upsertTeam(team: TeamDTO): TeamDTO

    /**
     * save team if it doesn't exist or return existing. Return DAO
     */
    fun ensureTeam(team: TeamDTO): Team

    /**
     * Add user to a team.
     * @param teamName - team teamName
     * @param email - user's email.
     */
    fun addUserToTeam(teamName: String, email: String)

    fun addUserToTeams(email: String, teams: List<TeamDTO>)

    fun removeUserFromTeam(name: String, userDto: UserDTO)

    fun deleteTeam(name: String)

    fun getAll(): List<TeamDTO>

    fun getAllForUser(): List<TeamDTO>

    fun findByName(name: String): TeamDTO?
}
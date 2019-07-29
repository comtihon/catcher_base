package com.catcher.base.service.team

import com.catcher.base.data.dao.Team
import com.catcher.base.data.dao.User
import com.catcher.base.data.dto.TeamDTO
import com.catcher.base.data.dto.UserDTO
import com.catcher.base.data.repository.TeamRepository
import com.catcher.base.data.repository.UserRepository
import com.catcher.base.exception.TeamNotFoundException
import com.catcher.base.exception.UserNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// TODO async
@Service
class TeamServiceImpl(@Autowired val teamRepository: TeamRepository,
                      @Autowired val userRepository: UserRepository) : TeamService {
    override fun upsertTeam(team: TeamDTO): TeamDTO {
        return teamRepository.save(team.toDAO()).toDTO()
    }

    override fun ensureTeam(team: TeamDTO): Team {
        return teamRepository.findByIdOrNull(team.name) ?: teamRepository.save(team.toDAO())
    }

    @Transactional
    override fun addUserToTeam(teamName: String, email: String) {
        val team: Team = teamRepository.findById(teamName).orElseThrow { throw TeamNotFoundException() }
        val user: User = userRepository.findById(email).orElseThrow { throw UserNotFoundException() }
        team.users.add(user)
    }

    @Transactional
    override fun addUserToTeams(email: String, teams: List<TeamDTO>) {
        val user: User = userRepository.findByIdOrNull(email)!!
        teams.forEach { teamRepository.findByIdOrNull(it.name)!!.users.add(user) }
    }

    @Transactional
    override fun removeUserFromTeam(name: String, userDto: UserDTO) {
        val team: Team = teamRepository.findById(name).orElseThrow { throw TeamNotFoundException() }
        val user: User = userRepository.findById(userDto.email).orElseThrow { throw UserNotFoundException() }
        team.users.remove(user)
    }

    override fun deleteTeam(name: String) {
        teamRepository.deleteById(name)
    }

    override fun getAll(): List<TeamDTO> {
        return teamRepository.findAll().map { it.toDTO() }
    }

    override fun getAllForUser(): List<TeamDTO> {
        return teamRepository.getAllForCurrentUser().map { it.toDTO() }
    }

    override fun findByName(name: String): TeamDTO? {
        return teamRepository.findById(name).map { it.toDTO() }.orElse(null)
    }
}
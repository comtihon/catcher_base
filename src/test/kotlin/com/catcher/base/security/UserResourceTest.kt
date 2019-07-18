package com.catcher.base.security

import com.catcher.base.FunctionalTest
import com.catcher.base.data.dao.Team
import com.catcher.base.data.dto.TeamDTO
import com.catcher.base.data.dto.UserDTO
import com.catcher.base.data.repository.TeamRepository
import com.catcher.base.service.project.ProjectService
import com.catcher.base.service.team.TeamService
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class UserResourceTest : FunctionalTest() {

    @Autowired
    lateinit var teamRepository: TeamRepository

    @Autowired
    lateinit var teamService: TeamService

    @Autowired
    lateinit var projectService: ProjectService

    /**
     * Should return only projects for current authenticated user
     */
    @Test
    fun getUserProjects() {
        val project1 = projectService.newProject(newProjectDTO("test_project1"))
        val project2 = projectService.newProject(newProjectDTO("test_project2"))

        val team1 = TeamDTO("test_team1", emptyList())
        teamService.upsertTeam(team1)
        teamService.addUserToTeam(team1.name, userEmail)

        val team2 = TeamDTO("test_team2", emptyList())
        teamService.upsertTeam(team2)

        projectService.addTeamToProject(project1.projectId!!, team1)
        projectService.addTeamToProject(project2.projectId!!, team2)

        val resultUser = getToken(userEmail, userPass).run {
            getWithToken("/api/v1/project", this, List::class.java)
        }
        Assert.assertTrue(resultUser.body!!.map { (it as Map<*, *>)["name"] }.contains(project1.name))
        Assert.assertTrue(resultUser.body!!.map { (it as Map<*, *>)["projectId"] }.contains(project1.projectId))
        Assert.assertEquals(1, resultUser.body!!.size)
    }

    /**
     * Should return only teams for current authenticated user
     */
    @Test
    fun getUserTeams() {
        val first = teamRepository.save(Team("first", mutableSetOf(), mutableSetOf()))
        val second = teamRepository.save(Team("second", mutableSetOf(), mutableSetOf()))
        teamRepository.save(Team("third", mutableSetOf(), mutableSetOf()))
        teamRepository.save(Team("fourth", mutableSetOf(), mutableSetOf()))
        val user = UserDTO(userEmail, null, null, null)
        teamService.addUserToTeam(first.name, user.email)
        teamService.addUserToTeam(second.name, user.email)
        val resultUser = getToken(userEmail, userPass).run {
            getWithToken("/api/v1/team/my", this, List::class.java)
        }
        Assert.assertTrue(resultUser.body!!.map { (it as Map<*, *>)["name"] }.contains(first.name))
        Assert.assertTrue(resultUser.body!!.map { (it as Map<*, *>)["name"] }.contains(second.name))
        Assert.assertEquals(2, resultUser.body!!.size)
    }
}
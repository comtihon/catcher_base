package com.catcher.base.controller

import com.catcher.base.IntegrationTest
import com.catcher.base.data.dto.TeamDTO
import com.catcher.base.data.dto.UserDTO
import com.catcher.base.data.repository.ProjectRepository
import com.catcher.base.data.repository.TeamRepository
import com.catcher.base.service.project.ProjectService
import com.catcher.base.service.team.TeamService
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*

class ProjectControllerTest : IntegrationTest() {

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var projectService: ProjectService

    @Autowired
    lateinit var teamService: TeamService

    @Autowired
    lateinit var teamRepository: TeamRepository

    /**
     * Team is added to a project. All team members now see this project.
     */
    @Test
    fun addTeamToProjectAsAdmin() {
        val project = projectService.newProject(newProjectDTO("test_project1"))
        val team = teamService.newTeam(TeamDTO("test_team1", emptyList()))
        teamService.addUserToTeam(team.name, UserDTO(userEmail, null, null, null))
        val userToken = getToken(userEmail, userPass)
        // get before team added - no projects returned
        val result = getWithToken("/api/v1/project", userToken, List::class.java)
        Assert.assertEquals(HttpStatus.OK, result.statusCode)
        Assert.assertTrue(result.body!!.isEmpty())
        // add team
        val adminToken = getToken(adminEmail, adminPass)
        val addResult = putWithToken("/api/v1/project/${project.projectId}/add_team", team, adminToken, String::class.java)
        Assert.assertEquals(HttpStatus.OK, addResult.statusCode)
        // get after team added - project is returned
        val resultAfter = getWithToken("/api/v1/project", userToken, List::class.java)
        Assert.assertEquals(HttpStatus.OK, resultAfter.statusCode)
        Assert.assertTrue(resultAfter.body!!.isNotEmpty())
        Assert.assertEquals((resultAfter.body!![0] as Map<*, *>)["projectId"], project.projectId)
    }

    /**
     * Admin can create a project
     */
    @Test
    fun createProjectAsAdmin() {
        val project = newProjectDTO("test_project2", ".")
        val result = getToken(adminEmail, adminPass).run {
            postWithToken("/api/v1/project", project, this, String::class.java)
        }
        Assert.assertEquals(HttpStatus.OK, result.statusCode)
        val projectFound = projectRepository.findProjectByName("test_project2")
        Assert.assertNotNull(projectFound)
    }

    /**
     * User won't access project if his team was removed from it
     */
    @Test
    fun removeOneTeamFromProject() {
        val project = projectService.newProject(newProjectDTO("test_project3"))
        val team = teamService.newTeam(TeamDTO("test_team3", emptyList()))
        teamService.addUserToTeam(team.name, UserDTO(userEmail, null, null, null))
        projectService.addTeamToProject(project.projectId!!, team)
        val userToken = getToken(userEmail, userPass)
        // get before team removed - project is returned
        val result = getWithToken("/api/v1/project", userToken, List::class.java)
        Assert.assertEquals(HttpStatus.OK, result.statusCode)
        Assert.assertTrue(result.body!!.isNotEmpty())
        Assert.assertEquals((result.body!![0] as Map<*, *>)["projectId"], project.projectId)
        // remove team
        val adminToken = getToken(adminEmail, adminPass)
        val addResult = putWithToken("/api/v1/project/${project.projectId}/del_team", team, adminToken, String::class.java)
        Assert.assertEquals(HttpStatus.OK, addResult.statusCode)
        // no projects for this user
        val resultAfter = getWithToken("/api/v1/project", userToken, List::class.java)
        Assert.assertEquals(HttpStatus.OK, resultAfter.statusCode)
        Assert.assertTrue(resultAfter.body!!.isEmpty())
    }

    /**
     * Deleted project shouldn't be returned.
     */
    @Test
    fun deleteProjectWithTeamAssigned() {
        val project = projectService.newProject(newProjectDTO("test_project4"))
        val team = teamService.newTeam(TeamDTO("test_team4", emptyList()))
        teamService.addUserToTeam(team.name, UserDTO(userEmail, null, null, null))
        projectService.addTeamToProject(project.projectId!!, team)
        val userToken = getToken(userEmail, userPass)
        // get before team removed - project is returned
        val result = getWithToken("/api/v1/project", userToken, List::class.java)
        Assert.assertEquals(HttpStatus.OK, result.statusCode)
        Assert.assertTrue(result.body!!.isNotEmpty())
        Assert.assertEquals((result.body!![0] as Map<*, *>)["projectId"], project.projectId)
        // delete project
        val adminToken = getToken(adminEmail, adminPass)
        val addResult = delWithToken("/api/v1/project/${project.projectId}", adminToken, String::class.java)
        Assert.assertEquals(HttpStatus.OK, addResult.statusCode)
        // no projects for this user
        val resultAfter = getWithToken("/api/v1/project", userToken, List::class.java)
        Assert.assertEquals(HttpStatus.OK, resultAfter.statusCode)
        Assert.assertTrue(resultAfter.body!!.isEmpty())
        // check the database. Project deleted, no projects in team.
        val found = projectRepository.findById(project.projectId!!)
        Assert.assertTrue(found.isEmpty)
        val foundTeam = teamRepository.findById(team.name)
        Assert.assertFalse(foundTeam.isEmpty)
    }
}
package com.catcher.base.controller

import com.catcher.base.IntegrationTest
import com.catcher.base.data.dto.TeamDTO
import com.catcher.base.data.repository.ProjectRepository
import com.catcher.base.data.repository.TeamRepository
import com.catcher.base.service.project.ProjectService
import com.catcher.base.service.team.TeamService
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup

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
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = ["classpath:clean_db.sql"])
    fun addTeamToProjectAsAdmin() {
        val project = projectService.newProject(newProjectDTO("test_project1"))
        val team = teamService.upsertTeam(TeamDTO("test_team1", emptyList()))
        teamService.addUserToTeam(team.name, userEmail)
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
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = ["classpath:clean_db.sql"])
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
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = ["classpath:clean_db.sql"])
    fun removeOneTeamFromProject() {
        val project = projectService.newProject(newProjectDTO("test_project3"))
        val team = teamService.upsertTeam(TeamDTO("test_team3", emptyList()))
        teamService.addUserToTeam(team.name, userEmail)
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
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = ["classpath:clean_db.sql"])
    fun deleteProjectWithTeamAssigned() {
        val project = projectService.newProject(newProjectDTO("test_project4"))
        val team = teamService.upsertTeam(TeamDTO("test_team4", emptyList()))
        teamService.addUserToTeam(team.name, userEmail)
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

    /**
     * Admin can get all projects. Normal user only projects available for his team.
     */
    @Test
    @SqlGroup(Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = ["classpath:clean_db.sql"]),
            Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:multiple_runs.sql"]))
    fun getAllForAdminAndCurrentForUser() {
        val tokenA = getToken("userA@test.de", "test")
        var result = getWithToken("/api/v1/project", tokenA, List::class.java)
        Assert.assertEquals(HttpStatus.OK, result.statusCode)
        var projects = result.body
        Assert.assertEquals(1, projects.size)
        Assert.assertEquals("test1", (projects[0] as Map<String, List<Map<String, *>>>)["name"])

        val tokenB = getToken("userB@test.de", "test")
        result = getWithToken("/api/v1/project", tokenB, List::class.java)
        Assert.assertEquals(HttpStatus.OK, result.statusCode)
        projects = result.body
        Assert.assertEquals(1, projects.size)
        Assert.assertEquals("test2", (projects[0] as Map<String, List<Map<String, *>>>)["name"])

        val adminToken = getToken(adminEmail, adminPass)
        result = getWithToken("/api/v1/project", adminToken, List::class.java)
        Assert.assertEquals(HttpStatus.OK, result.statusCode)
        projects = result.body
        Assert.assertEquals(2, projects.size)
        Assert.assertEquals("test1", (projects[0] as Map<String, List<Map<String, *>>>)["name"])
        Assert.assertEquals("test2", (projects[1] as Map<String, List<Map<String, *>>>)["name"])
    }

    /**
     * 2 projects. each has 2 tests.
     * first project's first test has 3 runs. The other none.
     * second project's test both have 1 run each.
     */
    @Test
    @SqlGroup(Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = ["classpath:clean_db.sql"]),
            Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:multiple_runs.sql"]))
    fun getLastRunsForTestsInProject() {
        val token = getToken("userA@test.de", "test")
        val result = getWithToken("/api/v1/project", token, List::class.java)
        Assert.assertEquals(HttpStatus.OK, result.statusCode)
        val projects = result.body
        Assert.assertEquals(1, projects.size)
        val tests = (projects[0] as Map<String, List<Map<String, *>>>)["tests"]
        Assert.assertEquals(2, tests!!.size)
        Assert.assertEquals("test1_1", tests[0]["name"])
        Assert.assertEquals("test1_2", tests[1]["name"])
        Assert.assertEquals(3, (tests[0]["lastRun"] as Map<String, *>)["id"])
        Assert.assertNull(tests[1]["lastRun"])
    }
}
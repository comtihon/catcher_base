package com.catcher.base.controller

import com.catcher.base.IntegrationTest
import com.catcher.base.data.entity.Team
import com.catcher.base.data.entity.User
import com.catcher.base.data.dto.TeamDTO
import com.catcher.base.data.repository.TeamRepository
import com.catcher.base.service.team.TeamService
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus


class TeamControllerTest : IntegrationTest() {

    @Autowired
    lateinit var teamRepository: TeamRepository

    @Autowired
    lateinit var teamService: TeamService

    /**
     * Team can be created
     */
    @Test
    fun newTeam() {
        val adminToken = getToken(adminEmail, adminPass)
        val res = postWithToken("/api/v1/team", TeamDTO("test_team", emptyList()), adminToken, TeamDTO::class.java)
        Assert.assertEquals(res.body!!.name, "test_team")
        val found = teamRepository.findById("test_team")
        Assert.assertTrue(found.isPresent)
    }


    /**
     * All teams can be listed
     */
    @Test
    fun getAll() {
        val first = teamRepository.save(Team("first", mutableSetOf(), mutableSetOf()))
        val second = teamRepository.save(Team("second", mutableSetOf(), mutableSetOf()))
        val userToken = getToken(userEmail, userPass)
        val res = getWithToken("/api/v1/team", userToken, List::class.java)
        Assert.assertEquals(2, res.body!!.size)
        Assert.assertTrue(res.body!!.map { (it as Map<*, *>)["name"] }.contains(first.name))
        Assert.assertTrue(res.body!!.map { (it as Map<*, *>)["name"] }.contains(second.name))
    }

    /**
     * User can be added to the team
     */
    @Test
    fun addUserToTeam() {
        val user = userRepository.save(User(email = "test@test.de", name = "test_user",
                phash = passwordEncoder.encode(userPass), lastLogin = null,
                role = roleRepository.findByName("user")!!, teams = emptySet()))
        val userToken = getToken(user.email, userPass)
        val team = teamRepository.save(Team("first", mutableSetOf(), mutableSetOf()))
        val res = getWithToken("/api/v1/team/${team.name}", userToken, TeamDTO::class.java)
        Assert.assertNotNull(res.body)
        Assert.assertTrue(res.body!!.users.isEmpty())

        val addRes = getToken(adminEmail, adminPass).run {
            putWithToken("/api/v1/team/${team.name}/add_user", user.toDTO(), this, String::class.java)
        }
        Assert.assertEquals(HttpStatus.OK, addRes.statusCode)
        val res2 = getWithToken("/api/v1/team/${team.name}", userToken, TeamDTO::class.java)
        Assert.assertNotNull(res2.body)
        Assert.assertEquals(1, res2.body!!.users.size)
        Assert.assertTrue(res2.body!!.users.contains(user.toDTO()))
    }

    /**
     * Team can be deleted
     */
    @Test
    fun delUserFromTeam() {
        val first = teamRepository.save(Team("first", mutableSetOf(), mutableSetOf()))
        val second = teamRepository.save(Team("second", mutableSetOf(), mutableSetOf()))
        val user = userRepository.save(User(email = "first@test.de", name = "first",
                phash = passwordEncoder.encode(userPass), lastLogin = null,
                role = roleRepository.findByName("user")!!, teams = emptySet()))
        teamService.addUserToTeam(first.name, user.email)
        teamService.addUserToTeam(second.name, user.email)

        val userToken = getToken(user.email, userPass)
        val res = getWithToken("/api/v1/team/${first.name}", userToken, TeamDTO::class.java)
        Assert.assertEquals(1, res.body!!.users.size)
        Assert.assertTrue(res.body!!.users.contains(user.toDTO()))

        val delRes = getToken(adminEmail, adminPass).run {
            putWithToken("/api/v1/team/${first.name}/del_user", user.toDTO(), this, String::class.java)
        }
        Assert.assertEquals(HttpStatus.OK, delRes.statusCode)
        val res2 = getWithToken("/api/v1/team/${first.name}", userToken, TeamDTO::class.java)
        Assert.assertNotNull(res2.body)
        Assert.assertTrue(res2.body!!.users.isEmpty())
        // user has membership only in second team
        val myRes = getWithToken("/api/v1/team/my", userToken, List::class.java)
        Assert.assertFalse(myRes.body!!.map { (it as Map<*, *>)["name"] }.contains(first.name))
        Assert.assertTrue(myRes.body!!.map { (it as Map<*, *>)["name"] }.contains(second.name))
    }

    @Test
    fun delTeam() {
        val team1 = teamRepository.save(Team("team1", mutableSetOf(), mutableSetOf()))
        val team2 = teamRepository.save(Team("team2", mutableSetOf(), mutableSetOf()))
        val user = userRepository.save(User(email = "first@test.de", name = "first",
                phash = passwordEncoder.encode(userPass), lastLogin = null,
                role = roleRepository.findByName("user")!!, teams = emptySet()))
        teamService.addUserToTeam(team1.name, user.email)
        teamService.addUserToTeam(team2.name, user.email)

        val delRes = getToken(adminEmail, adminPass).run {
            delWithToken("/api/v1/team/${team1.name}", this, String::class.java)
        }
        Assert.assertEquals(HttpStatus.OK, delRes.statusCode)
        // only team2 in user's teams
        val userToken = getToken(user.email, userPass)
        val myRes = getWithToken("/api/v1/team/my", userToken, List::class.java)
        Assert.assertEquals(1, myRes.body!!.size)
        Assert.assertTrue(myRes.body!!.map { (it as Map<*, *>)["name"] }.contains(team2.name))
        // team1 doesn't exist
        val team1Res = getWithToken("/api/v1/team/${team1.name}", userToken, String::class.java)
        Assert.assertEquals(HttpStatus.NOT_FOUND, team1Res.statusCode)
    }
}

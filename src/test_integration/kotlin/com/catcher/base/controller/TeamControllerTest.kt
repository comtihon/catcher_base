package com.catcher.base.controller

import com.catcher.base.IntegrationTest
import com.catcher.base.data.dto.TeamDTO
import com.catcher.base.data.repository.TeamRepository
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired


class TeamControllerTest : IntegrationTest() {

    @Autowired
    lateinit var teamRepository: TeamRepository

    @Test
    fun newTeam() {
        val adminToken = getToken(adminEmail, adminPass)
        val res = postWithToken("/api/v1/team", TeamDTO("test_team"), adminToken, TeamDTO::class.java)
        Assert.assertEquals(res.body!!.name, "test_team")
        val found = teamRepository.findById("test_team")
        Assert.assertTrue(found.isPresent)
    }

    @Test
    fun getAll() {

    }

    @Test
    fun addUserToTeam() {

    }

    @Test
    fun delUserFromTeam() {

    }

    @Test
    fun delTeam() {

    }
}

package com.catcher.base.controller

import com.catcher.base.IntegrationTest
import com.catcher.base.data.dao.User
import com.catcher.base.data.dto.ProjectDTO
import com.catcher.base.data.repository.RoleRepository
import com.catcher.base.data.repository.UserRepository
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import java.net.URL
import org.springframework.boot.web.server.LocalServerPort


class ProjectIntegrationTest : IntegrationTest() {

    val adminEmail = "test1@test.de"
    val adminPass = "test"
    val userEmail = "test2@test.de"
    val userPass = "test"
    var base: URL? = null

    @Before
    fun setUp() {
        userRepository.save(User(email = adminEmail, name = "test1",
                phash = passwordEncoder.encode(adminPass), lastLogin = null,
                roles = mutableSetOf(roleRepository.findByName("admin")!!), teams = emptySet()))
        userRepository.save(User(email = userEmail, name = "test2",
                phash = passwordEncoder.encode(userPass), lastLogin = null,
                roles = mutableSetOf(roleRepository.findByName("user")!!), teams = emptySet()))
        base = URL("http://localhost:$port")
    }

    @Test
    fun createNewProjectAndAddUsersToIdAsAdmin() {
        val project = ProjectDTO(null, "test_project1", null, ".")
        val template = TestRestTemplate(adminEmail, adminPass)
        val result = template.getForEntity(base.toString() + "/api/v1/project", String::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)
    }

    @Test
    fun removeOneTeamFromProject() {

    }

    @Test
    fun deleteTeamWithProjectsAssigned() {

    }
}
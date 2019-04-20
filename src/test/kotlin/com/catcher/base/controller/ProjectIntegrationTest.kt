package com.catcher.base.controller

import com.catcher.base.IntegrationTest
import com.catcher.base.data.dto.ProjectDTO
import com.catcher.base.data.repository.ProjectRepository
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.util.UriComponentsBuilder


class ProjectIntegrationTest : IntegrationTest() {

    @Autowired
    lateinit var projectRepository: ProjectRepository


    @Test
    fun addUsersToProjectAsAdmin() {
//        val project = ProjectDTO(null, "test_project1", null, ".")
//        val headers = HttpHeaders()
//        headers.add(HttpHeaders.AUTHORIZATION, "Bearer ADMIN")
//        headers.contentType = MediaType.APPLICATION_JSON
//        val uri = UriComponentsBuilder.fromPath("/api/v1/project")
//        val result = template.exchange(uri.build().toUri(), HttpMethod.GET,
//                HttpEntity<String>(headers), String::class.java)
//        Assert.assertEquals(HttpStatus.OK, result.statusCode)
    }

    @Test
    fun createProjectAsAdmin() {
        val token = getToken(adminEmail, adminPass)
        val headers = HttpHeaders()
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer ${token["access_token"]}")
        headers.contentType = MediaType.APPLICATION_JSON
        val project = ProjectDTO(null, "test_project1", null, ".")
        val request = HttpEntity(project, headers)
        val uri = UriComponentsBuilder.fromPath("/api/v1/project")
        val result = template.exchange(uri.build().toUri(), HttpMethod.POST, request, String::class.java)
        Assert.assertEquals(HttpStatus.OK, result.statusCode)
        val projectFound = projectRepository.findProjectByName("test_project1")
        Assert.assertNotNull(projectFound)
    }

    @Test
    fun removeOneTeamFromProject() {

    }

    @Test
    fun deleteTeamWithProjectsAssigned() {

    }
}
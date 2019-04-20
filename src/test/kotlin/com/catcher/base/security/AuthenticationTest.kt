package com.catcher.base.security

import com.catcher.base.IntegrationTest
import com.catcher.base.data.dto.ProjectDTO
import org.junit.Assert
import org.junit.Test
import org.springframework.http.*
import org.springframework.web.util.UriComponentsBuilder


class AuthenticationTest : IntegrationTest() {

    /**
     * Should not allow users with wrong credentials
     */
    @Test
    fun wrongCredentialsAuthFail() {
        val token = getToken(adminEmail, "wrongPassword")
        Assert.assertNull(token["access_token"])
        Assert.assertEquals(token["error"], "invalid_grant")
        Assert.assertEquals(token["error_description"], "Bad credentials")
    }

    /**
     * Should return token to user with right credentials
     */
    @Test
    fun properCredentialsAuthOK() {
        val token = getToken(adminEmail, adminPass)
        Assert.assertNotNull(token["access_token"])
        Assert.assertNull(token["error"])
    }

    /**
     * Authenticated user shouldn't access resources he doesn't have permissions for
     */
    @Test
    fun unauthorizedFail() {
        val token = getToken(userEmail, userPass)
        val headers = HttpHeaders()
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer ${token["access_token"]}")
        headers.contentType = MediaType.APPLICATION_JSON
        val project = ProjectDTO(null, "test_project1", null, ".")
        val request = HttpEntity(project, headers)
        val uri = UriComponentsBuilder.fromPath("/api/v1/project")
        val result = template.exchange(uri.build().toUri(), HttpMethod.POST, request, String::class.java)
        Assert.assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }

    /**
     * Should not allow access to protected resources without token
     */
    @Test
    fun unauthenticatedFail() {
        val uri = UriComponentsBuilder.fromPath("/api/v1/project")
        val result = template.exchange(uri.build().toUri(), HttpMethod.GET,
                HttpEntity<String>(HttpHeaders()), String::class.java)
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
    }

    /**
     * Should return protected resource for proper token
     */
    @Test
    fun authorizedOk() {
        val token = getToken(adminEmail, adminPass)
        val headers = HttpHeaders()
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer ${token["access_token"]}")
        headers.contentType = MediaType.APPLICATION_JSON
        val uri = UriComponentsBuilder.fromPath("/api/v1/project")
        val result = template.exchange(uri.build().toUri(), HttpMethod.GET,
                HttpEntity<String>(headers), String::class.java)
        Assert.assertEquals(HttpStatus.OK, result.statusCode)
    }
}
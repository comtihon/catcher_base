package com.catcher.base.security

import com.catcher.base.FunctionalTest
import com.catcher.base.data.dto.ProjectDTO
import org.junit.Assert
import org.junit.Test
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.util.UriComponentsBuilder


class AuthenticationTest : FunctionalTest() {

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
        val project = ProjectDTO(null, "test_project1", null, ".")
        val result = getToken(userEmail, userPass).run {
            postWithToken("/api/v1/project", project, this, String::class.java)
        }
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
        val resultAdmin = getToken(adminEmail, adminPass).run {
            getWithToken("/api/v1/project", this, String::class.java)
        }
        Assert.assertEquals(HttpStatus.OK, resultAdmin.statusCode)
        val resultUser = getToken(userEmail, userPass).run {
            getWithToken("/api/v1/project", this, String::class.java)
        }
        Assert.assertEquals(HttpStatus.OK, resultUser.statusCode)
    }

    private fun getToken(username: String, password: String): Map<*, *> {
        val request = LinkedMultiValueMap<String, String>()
        request.set("username", username)
        request.set("password", password)
        request.set("grant_type", "password")

        return template.withBasicAuth(clientId, secret)
                .postForObject(UriComponentsBuilder.fromPath("/oauth/token").build().toUri(),
                        request, Map::class.java)
    }

    private fun <T> postWithToken(path: String, body: Any, token: Map<*, *>, responseType: Class<T>): ResponseEntity<T> =
            withToken(path, token, body, HttpMethod.POST, responseType)

    private fun <T> getWithToken(path: String, token: Map<*, *>, responseType: Class<T>): ResponseEntity<T> =
            withToken(path, token, null, HttpMethod.GET, responseType)

    private fun <T> withToken(path: String, token: Map<*, *>, body: Any?, method: HttpMethod, responseType: Class<T>): ResponseEntity<T> {
        val headers = HttpHeaders()
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer ${token["access_token"]}")
        headers.contentType = MediaType.APPLICATION_JSON
        val uri = UriComponentsBuilder.fromPath(path)
        return template.exchange(uri.build().toUri(), method, HttpEntity(body, headers), responseType)
    }
}
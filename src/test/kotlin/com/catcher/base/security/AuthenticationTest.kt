package com.catcher.base.security

import com.catcher.base.FunctionalTest
import org.junit.Assert
import org.junit.Test
import org.springframework.http.*
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
        val project = newProjectDTO("test_project1", localPath = ".")
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
}
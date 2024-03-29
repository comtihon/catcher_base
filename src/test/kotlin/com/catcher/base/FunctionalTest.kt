package com.catcher.base

import com.catcher.base.data.entity.Project
import com.catcher.base.data.entity.User
import com.catcher.base.data.dto.ProjectDTO
import com.catcher.base.data.repository.PrivilegeRepository
import com.catcher.base.data.repository.RoleRepository
import com.catcher.base.data.repository.UserRepository
import com.catcher.base.service.project.ProjectScanner
import com.catcher.base.utils.recreateRoles
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.util.UriComponentsBuilder
import java.io.FileWriter
import java.nio.file.Path
import java.nio.file.Paths


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
abstract class FunctionalTest {

    @Value("\${security.oauth2.client.client-id}")
    val clientId: String? = null

    @Value("\${security.oauth2.client.client-secret}")
    val secret: String? = null

    val testProjectDir: Path = Paths.get(
        java.io.File(".").canonicalPath,
        "src",
        "test",
        "resources",
        "data",
        this.javaClass.name
    )

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var template: TestRestTemplate

    @Autowired
    lateinit var privilegeRepository: PrivilegeRepository

    protected val adminEmail = "test1@test.de"
    protected val adminPass = "test"
    protected val userEmail = "test2@test.de"
    protected val userPass = "test"

    @Before
    fun setUp() {
        recreateRoles(roleRepository, privilegeRepository)
        userRepository.save(
            User(
                email = adminEmail, name = "test1",
                phash = passwordEncoder.encode(adminPass), lastLogin = null,
                role = roleRepository.findByName("admin")!!, teams = emptySet()
            )
        )
        userRepository.save(
            User(
                email = userEmail, name = "test2",
                phash = passwordEncoder.encode(userPass), lastLogin = null,
                role = roleRepository.findByName("user")!!, teams = emptySet()
            )
        )
    }

    @After
    fun tearDown() {
        Paths.get(
            java.io.File(".").canonicalPath,
            "src",
            "test",
            "resources",
            "data"
        ).toFile().deleteRecursively()
        Paths.get("projects").toFile().deleteRecursively()
    }

    fun addTest(testPath: Path, content: String = "") {
        testPath.parent.toFile().mkdirs()
        FileWriter(testPath.toFile()).use { it.write(content) }
    }

    protected fun getToken(username: String, password: String): Map<*, *> {
        val request = LinkedMultiValueMap<String, String>()
        request.set("username", username)
        request.set("password", password)
        request.set("grant_type", "password")

        return template.postForObject(
                UriComponentsBuilder.fromPath("/oauth/token").build().toUri(),
                request, Map::class.java
            )
    }

    protected fun <T> postWithToken(
        path: String,
        body: Any,
        token: Map<*, *>,
        responseType: Class<T>
    ): ResponseEntity<T> =
        withToken(path, token, body, HttpMethod.POST, responseType)

    protected fun <T> getWithToken(path: String, token: Map<*, *>, responseType: Class<T>): ResponseEntity<T> =
        withToken(path, token, null, HttpMethod.GET, responseType)

    private fun <T> withToken(
        path: String,
        token: Map<*, *>,
        body: Any?,
        method: HttpMethod,
        responseType: Class<T>
    ): ResponseEntity<T> {
        val headers = HttpHeaders()
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer ${token["access_token"]}")
        headers.contentType = MediaType.APPLICATION_JSON
        val uri = UriComponentsBuilder.fromPath(path)
        return template.exchange(uri.build().toUri(), method, HttpEntity(body, headers), responseType)
    }


    protected fun newProjectDTO(name: String, localPath: String? = null) = ProjectDTO(name, localPath)

    /**
     * Create project in standalone directory (src/test/resources)
     */
    protected fun createStubProject(name: String = "testProject"): Project {
        val projectDir = Paths.get(testProjectDir.toString(), name)
        projectDir.toFile().mkdirs()
        val testDir = Paths.get(projectDir.toString(), ProjectScanner.TEST_DIR)
        testDir.toFile().mkdirs()
        return Project(
            0,
            "testProject",
            null,
            projectDir.toString(),
            null,
            mutableSetOf(),
            mutableSetOf()
        )
    }
}
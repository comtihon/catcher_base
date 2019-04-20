package com.catcher.base

import com.catcher.base.data.dao.User
import com.catcher.base.data.repository.RoleRepository
import com.catcher.base.data.repository.UserRepository
import org.junit.After
import org.junit.Before
import org.junit.ClassRule
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.util.UriComponentsBuilder
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import java.nio.file.Paths
import java.time.Duration


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [IntegrationTest.Initializer::class])
abstract class IntegrationTest {

    @Value("\${security.oauth2.client.client-id}")
    val clientId: String? = null

    @Value("\${security.oauth2.client.client-secret}")
    val secret: String? = null

    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
    lateinit var passwordEncoder: PasswordEncoder
    @Autowired
    lateinit var roleRepository: RoleRepository
    @Autowired
    lateinit var template: TestRestTemplate


    protected val adminEmail = "test1@test.de"
    protected val adminPass = "test"
    protected val userEmail = "test2@test.de"
    protected val userPass = "test"

    @LocalServerPort
    var port: Int = 0

    @Before
    fun setUp() {
        userRepository.save(User(email = adminEmail, name = "test1",
                phash = passwordEncoder.encode(adminPass), lastLogin = null,
                role = roleRepository.findByName("admin")!!, teams = emptySet()))
        userRepository.save(User(email = userEmail, name = "test2",
                phash = passwordEncoder.encode(userPass), lastLogin = null,
                role = roleRepository.findByName("user")!!, teams = emptySet()))
    }

    @After
    fun tearDown() {
        Paths.get("projects").toFile().deleteRecursively()
    }

    fun getToken(username: String, password: String): Map<*, *> {
        val request = LinkedMultiValueMap<String, String>()
        request.set("username", username)
        request.set("password", password)
        request.set("grant_type", "password")

        return template.withBasicAuth(clientId, secret)
                .postForObject(UriComponentsBuilder.fromPath("/oauth/token").build().toUri(),
                        request, Map::class.java)
    }

    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=jdbc:postgresql://${IntegrationTest.postgres.containerIpAddress}:${IntegrationTest.postgres.getMappedPort(5432)}/catcher?createDatabaseIfNotExist=true&amp;amp;useUnicode=true&amp;amp;characterEncoding=utf-8&amp;amp;autoReconnect=true"

            ).applyTo(configurableApplicationContext.environment)
        }
    }

    companion object {
        @ClassRule
        @JvmField
        val postgres: KPostgreSQLContainer = KPostgreSQLContainer("postgres:11.1")
                .withDatabaseName("catcher")
                .withUsername("test")
                .withPassword("test")
                .withExposedPorts(5432)
                .waitingFor(LogMessageWaitStrategy()
                        .withRegEx(".*database system is ready to accept connections.*\\s")
                        .withTimes(2)
                        .withStartupTimeout(Duration.ofSeconds(60, 0)))
    }

    // workaround for https://youtrack.jetbrains.com/issue/KT-17186
    class KPostgreSQLContainer(image: String) : PostgreSQLContainer<KPostgreSQLContainer>(image)
}
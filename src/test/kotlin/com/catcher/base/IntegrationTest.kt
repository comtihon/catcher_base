package com.catcher.base

import com.catcher.base.data.repository.RoleRepository
import com.catcher.base.data.repository.UserRepository
import org.junit.After
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Paths

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
abstract class IntegrationTest {

    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
    lateinit var passwordEncoder: PasswordEncoder
    @Autowired
    lateinit var roleRepository: RoleRepository
    @Autowired
    lateinit var template: TestRestTemplate

    @LocalServerPort
    var port: Int = 0

    @After
    fun tearDown() {
        Paths.get("projects").toFile().deleteRecursively()
    }
}
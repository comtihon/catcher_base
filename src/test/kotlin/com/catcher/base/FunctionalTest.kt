package com.catcher.base

import com.catcher.base.data.dao.Privilege
import com.catcher.base.data.dao.Role
import com.catcher.base.data.dao.User
import com.catcher.base.data.repository.PrivilegeRepository
import com.catcher.base.data.repository.RoleRepository
import com.catcher.base.data.repository.UserRepository
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit4.SpringRunner
import java.nio.file.Paths


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class FunctionalTest {

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
    @Autowired
    lateinit var privilegeRepository: PrivilegeRepository

    protected val adminEmail = "test1@test.de"
    protected val adminPass = "test"
    protected val userEmail = "test2@test.de"
    protected val userPass = "test"

    @Before
    fun setUp() {
        if (userRepository.findAll().toList().isEmpty()) {
            val lt = privilegeRepository.save(Privilege(0, "launch_tests"))
            val mt = privilegeRepository.save(Privilege(0, "modify_tests"))
            val ms = privilegeRepository.save(Privilege(0, "modify_steps"))
            val et = privilegeRepository.save(Privilege(0, "edit_templates"))
            val mte = privilegeRepository.save(Privilege(0, "modify_teams"))
            val mp = privilegeRepository.save(Privilege(0, "modify_projects"))

            roleRepository.save(Role(0, "admin", mutableSetOf(),
                    mutableSetOf(lt, mt, ms, et, mte, mp)))
            roleRepository.save(Role(0, "user", mutableSetOf(),
                    mutableSetOf(lt, mt, ms)))
        }
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
}
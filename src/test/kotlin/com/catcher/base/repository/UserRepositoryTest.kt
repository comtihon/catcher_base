package com.catcher.base.repository

import com.catcher.base.data.dao.User
import com.catcher.base.data.repository.PrivilegeRepository
import com.catcher.base.data.repository.RoleRepository
import com.catcher.base.data.repository.UserRepository
import com.catcher.base.utils.recreateRoles
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@DataJpaTest
class UserRepositoryTest {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var privilegeRepository: PrivilegeRepository

    @Before
    fun setUp() {
        recreateRoles(roleRepository, privilegeRepository)
    }

    /**
     * Admins should be found by role name
     */
    @Test
    fun testFindAdmins() {
        Assert.assertTrue(userRepository.findAdmins().isEmpty())
        val user = User(email = "user",
                name = "user",
                phash = "user",
                teams = emptySet(),
                lastLogin = null,
                role = roleRepository.findByName("user")!!)
        userRepository.save(user)
        Assert.assertTrue(userRepository.findAdmins().isEmpty())
        val admin = User(email = "admin",
                name = "admin",
                phash = "admin",
                teams = emptySet(),
                lastLogin = null,
                role = roleRepository.findByName("admin")!!)
        userRepository.save(admin)
        val admins = userRepository.findAdmins()
        Assert.assertFalse(admins.isEmpty())
        Assert.assertEquals(admin, admins[0])
    }

}
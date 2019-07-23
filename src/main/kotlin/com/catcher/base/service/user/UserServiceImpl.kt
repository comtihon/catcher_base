package com.catcher.base.service.user

import com.catcher.base.data.dao.User
import com.catcher.base.data.dto.UserDTO
import com.catcher.base.data.repository.RoleRepository
import com.catcher.base.data.repository.UserRepository
import com.catcher.base.exception.EmailExistsException
import com.catcher.base.exception.UserNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class UserServiceImpl(@Autowired val userRepository: UserRepository,
                      @Autowired val passwordEncoder: PasswordEncoder,
                      @Autowired val roleRepository: RoleRepository) : UserService {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("#{environment.ADMIN_USER}")
    private val adminUser: String? = null

    @Value("#{environment.ADMIN_PASS}")
    private val adminPass: String? = null

    override fun registerUser(user: UserDTO): UserDTO {
        val existing: User? = userRepository.findById(user.email).orElse(null)
        if (existing != null)
            throw EmailExistsException("User with name %s exists".format(user.email))
        // TODO do not allow create users with non-user role (except creating user is admin himself)!
        val userDao = User(email = user.email,
                name = user.name ?: "",
                phash = passwordEncoder.encode(user.password.orEmpty()),
                teams = emptySet(),
                lastLogin = null,
                role = roleRepository.findByName(user.role?.name ?: "user")!!)
        userRepository.save(userDao)
        return userDao.toDTO()
    }

    override fun findByEmail(email: String): UserDTO {
        return userRepository.findById(email)
                .orElseThrow { throw UserNotFoundException() }
                .toDTO()
    }

    @PostConstruct
    fun init() {
        if (!adminPass.isNullOrBlank() && !adminUser.isNullOrBlank()) { // admin create user on startup
            if (userRepository.findAdmins().isEmpty()) {
                log.info("Creating admin user")
                val user = User(email = adminUser,
                        name = "admin",
                        phash = passwordEncoder.encode(adminPass),
                        teams = emptySet(),
                        lastLogin = null,
                        role = roleRepository.findByName("admin")!!)
                userRepository.save(user)
            }
        }
    }
}
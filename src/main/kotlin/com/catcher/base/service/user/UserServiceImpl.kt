package com.catcher.base.service.user

import com.catcher.base.data.dao.User
import com.catcher.base.data.dto.UserDTO
import com.catcher.base.data.repository.RoleRepository
import com.catcher.base.data.repository.UserRepository
import com.catcher.base.exception.EmailExistsException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(@Autowired val userRepository: UserRepository,
                      @Autowired val passwordEncoder: PasswordEncoder,
                      @Autowired val roleRepository: RoleRepository) : UserService {
    override fun registerUser(user: UserDTO): User {
        val existing: User? = userRepository.findById(user.email).orElse(null)
        if (existing != null)
            throw EmailExistsException("User with name %s exists".format(user.email))
        // TODO do not allow create users with non-user role (except creating user is admin himself)!
        val userDao = User(email = user.email,
                name = user.name ?: "",
                phash = passwordEncoder.encode(user.password.orEmpty()),
                teams = emptySet(),
                lastLogin = null,
                role = roleRepository.findByName(user.role ?: "user")!!)
        userRepository.save(userDao)
        return userDao
    }
}
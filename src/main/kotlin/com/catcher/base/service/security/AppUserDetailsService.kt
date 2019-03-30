package com.catcher.base.service.security

import com.catcher.base.data.dao.User
import com.catcher.base.data.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AppUserDetailsService(@Autowired val userRepo: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user: User = userRepo.findById(username).orElseThrow { UsernameNotFoundException("User not found") }
        // TODO load roles and permissions eager https://www.baeldung.com/role-and-privilege-for-spring-security-registration
        return UserDetailsHolder(user)
    }
}
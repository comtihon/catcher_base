package com.catcher.base.service.security

import com.catcher.base.data.entity.User
import com.catcher.base.data.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
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
        // TODO use user entity : UserDetails
        return object : UserDetails {
            override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
                return user.role.privileges.map { SimpleGrantedAuthority(it.name) }.toMutableList()
            }

            override fun getUsername(): String {
                return user.email // return email here to use it in repository
            }

            override fun isCredentialsNonExpired(): Boolean {
                return true
            }

            override fun getPassword(): String {
                return user.phash
            }

            override fun isAccountNonExpired(): Boolean {
                return true
            }

            override fun isAccountNonLocked(): Boolean {
                return true
            }

            override fun isEnabled(): Boolean {
                return true // TODO check email confirmed
            }
        }
    }
}
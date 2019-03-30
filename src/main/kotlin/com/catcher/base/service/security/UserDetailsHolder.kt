package com.catcher.base.service.security

import com.catcher.base.data.dao.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority


class UserDetailsHolder(private val user: User) : UserDetails {
    override fun isEnabled(): Boolean {
        return true // TODO check email confirmed
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

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return user.roles.flatMap { role -> role.privileges.map { SimpleGrantedAuthority(it.name) } }.toMutableList()
    }
}
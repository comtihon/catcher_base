package com.catcher.base.data.repository

import com.catcher.base.data.entity.Role
import org.springframework.data.repository.CrudRepository

interface RoleRepository : CrudRepository<Role, Int> {
    fun findByName(name: String): Role?
}
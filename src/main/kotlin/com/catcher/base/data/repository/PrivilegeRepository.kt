package com.catcher.base.data.repository

import com.catcher.base.data.dao.Privilege
import org.springframework.data.repository.CrudRepository

interface PrivilegeRepository : CrudRepository<Privilege, Int> {
    fun findByName(name: String): Privilege?
}
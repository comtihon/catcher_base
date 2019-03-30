package com.catcher.base.service.security.roles

import com.catcher.base.data.dto.RoleDTO

interface RolesService {

    fun getPrivileges(): List<String>

    fun getRole(id: Int): RoleDTO

    fun getRoles(): List<RoleDTO>

    fun newRole(roleDTO: RoleDTO): RoleDTO

    fun updateRole(roleDTO: RoleDTO): RoleDTO

    fun deleteRole(id: Int)
}
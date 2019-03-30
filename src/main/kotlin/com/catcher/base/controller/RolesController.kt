package com.catcher.base.controller

import com.catcher.base.data.dto.RoleDTO
import com.catcher.base.service.security.roles.RolesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/roles")
class RolesController(@Autowired val rolesService: RolesService) {

    @GetMapping("/privileges")
    fun getAllPrivileges(): List<String> {
        return rolesService.getPrivileges()
    }

    @GetMapping
    fun getAllRoles(): List<RoleDTO> {
        return rolesService.getRoles()
    }

    @GetMapping("/{id}")
    fun getRole(@PathVariable id: Int): RoleDTO {
        return rolesService.getRole(id)
    }

    @PostMapping
    fun new(@Valid @RequestBody role: RoleDTO): RoleDTO {
        return rolesService.newRole(role)
    }

    @PutMapping
    fun change(@Valid @RequestBody role: RoleDTO): RoleDTO {
        return rolesService.updateRole(role)
    }

    @DeleteMapping("/{id}")
    fun removeRole(@PathVariable id: Int) {
        return rolesService.deleteRole(id)
    }
}
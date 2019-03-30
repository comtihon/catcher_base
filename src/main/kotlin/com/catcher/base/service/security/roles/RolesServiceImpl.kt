package com.catcher.base.service.security.roles

import com.catcher.base.data.dao.Privilege
import com.catcher.base.data.dto.RoleDTO
import com.catcher.base.data.repository.PrivilegeRepository
import com.catcher.base.data.repository.RoleRepository
import com.catcher.base.exception.RoleIdRequiredException
import com.catcher.base.exception.RoleNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RolesServiceImpl(@Autowired val roleRepository: RoleRepository,
                       @Autowired val privilegeRepository: PrivilegeRepository) : RolesService {
    override fun getPrivileges(): List<String> {
        return privilegeRepository.findAll().map(Privilege::name)
    }

    override fun getRole(id: Int): RoleDTO {
        return roleRepository.findById(id).map { it.toDTO() }.orElseThrow(::RoleNotFoundException)
    }

    override fun getRoles(): List<RoleDTO> {
        return roleRepository.findAll().map { it.toDTO() }
    }

    override fun newRole(roleDTO: RoleDTO): RoleDTO {
        return roleRepository.save(roleDTO.toDAO()).toDTO() // TODO testme in case of non-unique conflict (400 error)
    }

    override fun updateRole(roleDTO: RoleDTO): RoleDTO {
        if (roleDTO.id == null)
            throw RoleIdRequiredException()
        val role = roleRepository.findById(roleDTO.id).orElseThrow(::RoleNotFoundException)
        role.name = roleDTO.name
        val privileges = roleDTO.privileges.map { privilegeRepository.findByName(it)!! }.toMutableSet() // TODO testme
        return roleRepository.save(role.copy(privileges = privileges)).toDTO()  // TODO testme
    }

    override fun deleteRole(id: Int) {
        roleRepository.deleteById(id)
    }
}
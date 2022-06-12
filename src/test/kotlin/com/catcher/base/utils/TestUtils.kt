package com.catcher.base.utils

import com.catcher.base.data.entity.Privilege
import com.catcher.base.data.entity.Role
import com.catcher.base.data.repository.PrivilegeRepository
import com.catcher.base.data.repository.RoleRepository

fun recreateRoles(roleRepository: RoleRepository,
                  privilegeRepository: PrivilegeRepository) {
    if (privilegeRepository.findAll().toList().isEmpty()) {
        val lt = privilegeRepository.save(Privilege(0, "launch_tests"))
        val mt = privilegeRepository.save(Privilege(0, "modify_tests"))
        val ms = privilegeRepository.save(Privilege(0, "modify_steps"))
        val et = privilegeRepository.save(Privilege(0, "edit_templates"))
        val mte = privilegeRepository.save(Privilege(0, "modify_teams"))
        val mp = privilegeRepository.save(Privilege(0, "modify_projects"))

        roleRepository.save(Role(0, "admin", mutableSetOf(),
                mutableSetOf(lt, mt, ms, et, mte, mp)))
        roleRepository.save(Role(0, "user", mutableSetOf(),
                mutableSetOf(lt, mt, ms)))
    }
}
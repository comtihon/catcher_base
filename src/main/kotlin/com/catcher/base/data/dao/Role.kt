package com.catcher.base.data.dao

import com.catcher.base.data.dto.RoleDTO
import javax.persistence.*

@Entity
@Table(name = "roles")
data class Role(@Id @GeneratedValue(strategy = GenerationType.AUTO)
                val id: Int,
                var name: String,
                @ManyToMany val users: MutableSet<User>,
                @ManyToMany
                @JoinTable(name = "roles_privileges",
                        joinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")],
                        inverseJoinColumns = [JoinColumn(name = "privilege_id", referencedColumnName = "id")])
                val privileges: MutableSet<Privilege>) {
    fun toDTO(): RoleDTO {
        return RoleDTO(id, name, privileges.map { it.name })
    }
}
package com.catcher.base.data.dao

import com.catcher.base.data.dto.RoleDTO
import javax.persistence.*

@Entity
@Table(name = "roles")
data class Role(@Id @GeneratedValue(strategy = GenerationType.AUTO)
                val id: Int,
                var name: String,
                @OneToMany val users: MutableSet<User>,
                @ManyToMany(fetch = FetchType.EAGER)
                @JoinTable(name = "roles_privileges",
                        joinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")],
                        inverseJoinColumns = [JoinColumn(name = "privilege_id", referencedColumnName = "id")])
                val privileges: MutableSet<Privilege>) {
    fun toDTO(): RoleDTO {
        return RoleDTO(id, name, privileges.map { it.name })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Role

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "Role(id=$id, name='$name', privileges=$privileges)"
    }


}
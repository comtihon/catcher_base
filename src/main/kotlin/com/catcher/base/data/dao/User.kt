package com.catcher.base.data.dao

import com.catcher.base.data.dto.UserDTO
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(@Id val email: String,
                val name: String,
                @Column(length = 60)
                val phash: String,
                val lastLogin: LocalDateTime?,
                @ManyToMany
                @JoinTable(name = "users_teams",
                        joinColumns = [JoinColumn(name = "user_email", referencedColumnName = "email")],
                        inverseJoinColumns = [JoinColumn(name = "team_name", referencedColumnName = "name")])
                val teams: Set<Team>,
                @ManyToMany
                @JoinTable(name = "users_roles",
                        joinColumns = [JoinColumn(name = "user_email", referencedColumnName = "email")],
                        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")])
                val roles: MutableSet<Role>) {
    fun toDTO(): UserDTO {
        return UserDTO(email, null, name, roles.map(Role::name))
    }


}
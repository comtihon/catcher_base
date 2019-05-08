package com.catcher.base.data.dao

import com.catcher.base.data.dto.TeamDTO
import javax.persistence.*

@Entity
@Table(name = "teams")
data class Team(@Id val name: String,
                @ManyToMany(cascade = [CascadeType.PERSIST])
                val users: MutableSet<User>,
                @ManyToMany(mappedBy = "teams")
                val projects: MutableSet<Project>) {
    fun toDTO(): TeamDTO {
        return TeamDTO(name, users.map(User::toDTO))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Team

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "Team(name='$name')"
    }
}
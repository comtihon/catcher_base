package com.catcher.base.data.dao

import com.catcher.base.data.dto.TeamDTO
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "teams")
data class Team(@Id val name: String,
                @ManyToMany(mappedBy = "teams")
                val users: MutableSet<User>,
                @ManyToMany(mappedBy = "teams")
                val projects: MutableSet<Project>) {
    fun toDTO(): TeamDTO {
        return TeamDTO(name)
    }
}
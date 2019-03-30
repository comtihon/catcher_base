package com.catcher.base.data.dao

import com.catcher.base.data.dto.ProjectDTO
import javax.persistence.*

@Entity
@Table(name = "projects")
data class Project(@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
                   val id: Int,
                   var name: String,
                   val localPath: String,
                   var remotePath: String?,
                   @ManyToMany
                   @JoinTable(name = "projects_teams",
                           joinColumns = [JoinColumn(name = "project_id", referencedColumnName = "id")],
                           inverseJoinColumns = [JoinColumn(name = "team_name", referencedColumnName = "name")])
                   val teams: MutableSet<Team>,
                   @OneToMany(cascade = [CascadeType.REMOVE])
                   val tests: MutableSet<Test>) {
    fun toDTO(): ProjectDTO {
        return ProjectDTO(id, name, localPath, remotePath)
    }
}
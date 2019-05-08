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
                   @ManyToMany(cascade = [CascadeType.PERSIST])
                   val teams: MutableSet<Team>,
                   @OneToMany(cascade = [CascadeType.REMOVE])
                   val tests: MutableSet<Test>) {
    fun toDTO(): ProjectDTO {
        return ProjectDTO(id, name, remotePath, localPath, tests.map(Test::toDTO))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Project

        if (id != other.id) return false
        if (localPath != other.localPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + localPath.hashCode()
        return result
    }

    override fun toString(): String {
        return "Project(id=$id, name='$name', localPath='$localPath', remotePath=$remotePath)"
    }
}
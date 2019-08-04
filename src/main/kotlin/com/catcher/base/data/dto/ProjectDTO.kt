package com.catcher.base.data.dto

import com.catcher.base.data.dao.Project
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class ProjectDTO(val projectId: Int?,
                      @NotEmpty @NotNull
                      val name: String,
                      val description: String?,
                      val remotePath: String?,
                      var localPath: String?,
                      @NotEmpty @NotNull
                      val teams: List<TeamDTO>,
                      val tests: List<TestDTO>) {
    constructor(name: String, localPath: String?) :
            this(0, name, null, null,
                    localPath, emptyList<TeamDTO>(), emptyList<TestDTO>())

    fun toDAO(): Project {
        return Project(projectId ?: 0,
                name,
                description,
                localPath ?: "",
                remotePath ?: "",
                teams.map(TeamDTO::toDAO).toMutableSet(),
                mutableSetOf())
    }
}
package com.catcher.base.data.dto

import com.catcher.base.data.dao.Project
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class ProjectDTO(val projectId: Int?,
                      @NotEmpty @NotNull
                      val name: String,
                      val remotePath: String?,
                      var localPath: String?,
                      @NotEmpty @NotNull
                      val teams: List<TeamDTO>,
                      val tests: List<TestDTO>) {
    fun toDAO(): Project {
        return Project(projectId ?: 0,
                name,
                localPath ?: "",
                remotePath ?: "",
                teams.map(TeamDTO::toDAO).toMutableSet(),
                mutableSetOf())
    }
}
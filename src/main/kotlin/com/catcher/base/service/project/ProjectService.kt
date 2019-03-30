package com.catcher.base.service.project

import com.catcher.base.data.dto.ProjectDTO
import com.catcher.base.data.dto.TeamDTO

interface ProjectService {
    fun newProject(projectDto: ProjectDTO): ProjectDTO

    fun getAll(): List<ProjectDTO>

    fun findById(projectId: Int): ProjectDTO

    fun addTeamToProject(projectId: Int, teamDto: TeamDTO)

    fun removeTeamFromProject(projectId: Int, teamDto: TeamDTO)

    fun delProject(projectId: Int)
}
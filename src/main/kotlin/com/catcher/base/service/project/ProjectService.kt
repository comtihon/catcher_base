package com.catcher.base.service.project

import com.catcher.base.data.dto.ProjectDTO
import com.catcher.base.data.dto.TeamDTO

interface ProjectService {
    fun newProject(projectDto: ProjectDTO): ProjectDTO

    /**
     * If current user's role is admin - return all available projects
     * otherwise return only projects, whose teams has this user in.
     */
    fun getAllLimitedForNonAdmin(email: String): List<ProjectDTO>

    fun findById(projectId: Int): ProjectDTO

    fun addTeamToProject(projectId: Int, teamDto: TeamDTO)

    fun removeTeamFromProject(projectId: Int, teamDto: TeamDTO)

    fun delProject(projectId: Int)
}
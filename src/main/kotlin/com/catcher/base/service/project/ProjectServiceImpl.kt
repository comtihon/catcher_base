package com.catcher.base.service.project

import com.catcher.base.data.entity.Project
import com.catcher.base.data.entity.Team
import com.catcher.base.data.dto.ProjectDTO
import com.catcher.base.data.dto.TeamDTO
import com.catcher.base.data.repository.ProjectRepository
import com.catcher.base.data.repository.UserRepository
import com.catcher.base.exception.ProjectNotFoundException
import com.catcher.base.service.team.TeamService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.annotation.PostConstruct


// TODO Async
@Service
class ProjectServiceImpl(@Autowired val projectRepo: ProjectRepository,
                         @Autowired val userRepository: UserRepository,
                         @Autowired val teamService: TeamService,
                         @Autowired val scanner: ProjectScanner) : ProjectService {

    @Value("\${catcher.base.local_dir}")
    private val localDir: String? = null

    private fun projectDir(): Path = Paths.get(localDir, "projects")

    override fun delProject(projectId: Int) {
        val project = projectRepo.findByIdOrNull(projectId)
        if (project != null)
            Paths.get(project.localPath).toFile().deleteRecursively()
        projectRepo.deleteById(projectId)
    }

    /**
     * Create new project or update existing.
     * Replace project's teams (create them if not created).
     */
    @Transactional
    override fun newProject(projectDto: ProjectDTO): ProjectDTO {
        var existing = projectRepo.findProjectByName(projectDto.name)
        val isNew =
                if (existing == null) { // new project - create directories
                    projectDto.localPath = ensureDirectories(projectDto)
                    existing = projectDto.toDAO()
                    true
                } else { // update existing project (local path can't be updated) TODO allow?
                    existing.apply {
                        this.remotePath = projectDto.remotePath ?: ""
                        this.name = projectDto.name
                        this.description = projectDto.description
                    }
                    false
                }
        replaceProjectTeams(existing, projectDto.teams)
        val saved = projectRepo.save(existing)
        scanner.scanProject(saved, isNew) // index project & all it's tests.
        return saved.toDTO()
    }

    override fun getAllLimitedForNonAdmin(email: String): List<ProjectDTO> {
        val user = userRepository.findByIdOrNull(email)!!
        return if (user.role.name == "admin")
            projectRepo.findAll().map { it.toDTO() }
        else
            projectRepo.getAllForCurrentUser().map { it.toDTO() }
    }

    override fun findById(projectId: Int): ProjectDTO {
        return projectRepo.findByIdWithTests(projectId)?.toFullDTO() ?: throw ProjectNotFoundException()
    }

    @Transactional
    override fun addTeamToProject(projectId: Int, teamDto: TeamDTO) {
        val project: Project = projectRepo.findById(projectId).orElseThrow { throw ProjectNotFoundException() }
        val team: Team = teamService.ensureTeam(teamDto)
        project.teams.add(team)
    }

    @Transactional
    override fun removeTeamFromProject(projectId: Int, teamDto: TeamDTO) {
        val project: Project = projectRepo.findById(projectId).orElseThrow { throw ProjectNotFoundException() }
        val team: Team = teamService.ensureTeam(teamDto)
        project.teams.remove(team)
    }

    private fun replaceProjectTeams(project: Project, teamDTOs: List<TeamDTO>) {
        val teams = teamDTOs.map { teamService.ensureTeam(it) }
        project.teams = teams.toMutableSet()
    }

    /**
     * Ensure projects dir and scan all existing projects for updates.
     */
    // TODO job to do it periodically?
    @PostConstruct
    fun init() {
        val root = projectDir().toFile()
        if (!root.exists()) {
            root.mkdirs() || throw Exception("Can't create directory $localDir/projects")
        }
        // scan all paths found in project's dir
        Files.list(root.toPath()).forEach {
            val name = it.fileName.toString()
            var existing = projectRepo.findProjectByName(name)
            if (existing == null) { // new project - only index project as we have no information about remote
                existing = ProjectDTO(name, it.toString()).toDAO()
                projectRepo.save(existing)
                scanner.indexProject(existing)
            } else { // existing project
                scanner.scanProject(existing)
            }
        }
        // TODO mark deleted projects as deleted & offer autoclean on UI
    }

    /**
     * Creates directory tree for a new project
     */
    private fun ensureDirectories(projectDto: ProjectDTO): String {
        var localPath: String? = projectDto.localPath
        if (localPath == null || localPath.isBlank()) {
            // no custom local dir - use catcher's project's dir
            localPath = projectDir().toAbsolutePath().resolve(Paths.get(projectDto.name)).toString()
        }
        listOf(ProjectScanner.TEST_DIR, ProjectScanner.STEPS_DIR, ProjectScanner.RES_DIR)
                .forEach { Paths.get(localPath, it).toFile().mkdirs() }
        return localPath
    }
}
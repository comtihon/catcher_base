package com.catcher.base.service.project

import com.catcher.base.data.dao.Project
import com.catcher.base.data.dao.Team
import com.catcher.base.data.dto.ProjectDTO
import com.catcher.base.data.dto.TeamDTO
import com.catcher.base.data.repository.ProjectRepository
import com.catcher.base.data.repository.TeamRepository
import com.catcher.base.exception.ProjectNotFoundException
import com.catcher.base.exception.TeamNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.annotation.PostConstruct
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional


// TODO Async
@Service
class ProjectServiceImpl(@Autowired val projectRepo: ProjectRepository,
                         @Autowired val teamRepository: TeamRepository,
                         @Autowired val scanner: ProjectScanner) : ProjectService {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${catcher.base.local_dir}")
    private val localDir: String? = null

    private fun projectDir(): Path = Paths.get(localDir, "projects")

    override fun delProject(projectId: Int) {
        val project = projectRepo.findByIdOrNull(projectId)
        if (project != null)
            Paths.get(project.localPath).toFile().deleteRecursively()
        projectRepo.deleteById(projectId)
    }

    @Transactional
    override fun newProject(projectDto: ProjectDTO): ProjectDTO {
        var existing = projectRepo.findProjectByName(projectDto.name)
        val isNew =
                if (existing == null) { // new project - create directories
                    createNewProject(projectDto)
                    existing = projectDto.toDAO()
                    true
                } else { // update existing project
                    existing.apply {
                        this.remotePath = projectDto.remotePath ?: ""
                        this.name = projectDto.name
                    }
                    false
                }
        val saved = projectRepo.save(existing)
        scanner.scanProject(saved, isNew) // index project & all it's tests.
        return saved.toDTO()
    }

    override fun getAllForUser(): List<ProjectDTO> {
        return projectRepo.getAllForCurrentUser().map { it.toDTO() }
    }

    override fun findById(projectId: Int): ProjectDTO {
        return projectRepo.findById(projectId).map { it.toDTO() }.orElseThrow(::ProjectNotFoundException)
    }

    @Transactional
    override fun addTeamToProject(projectId: Int, teamDto: TeamDTO) {
        val team: Team = teamRepository.findById(teamDto.name).orElseThrow { throw TeamNotFoundException() }
        val project: Project = projectRepo.findById(projectId).orElseThrow { throw ProjectNotFoundException() }
        project.teams.add(team)
    }

    @Transactional
    override fun removeTeamFromProject(projectId: Int, teamDto: TeamDTO) {
        val team: Team = teamRepository.findById(teamDto.name).orElseThrow { throw TeamNotFoundException() }
        val project: Project = projectRepo.findById(projectId).orElseThrow { throw ProjectNotFoundException() }
        project.teams.remove(team)
    }

    /**
     * Ensure projects dir and scan all existing projects for updates.
     */
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
                existing = ProjectDTO(0, name, null, it.toString()).toDAO()
                projectRepo.save(existing)
                scanner.indexProject(existing)
            } else { // existing project
                scanner.scanProject(existing)
            }
        }
        // TODO remove deleted projects?
    }

    /**
     * Creates directory tree for a new project
     */
    private fun createNewProject(projectDto: ProjectDTO) {
        if (projectDto.localPath == null || projectDto.localPath!!.isBlank()) {
            // no custom local dir - use catcher's project's dir
            projectDto.localPath = projectDir().toAbsolutePath().resolve(Paths.get(projectDto.name)).toString()
        }
        with(projectDto) {
            Paths.get(localPath, ProjectScanner.TEST_DIR).toFile().mkdirs()
            Paths.get(localPath, ProjectScanner.STEPS_DIR).toFile().mkdirs()
            Paths.get(localPath, ProjectScanner.RES_DIR).toFile().mkdirs()
        }
    }
}
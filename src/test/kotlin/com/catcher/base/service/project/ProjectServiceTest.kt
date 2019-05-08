package com.catcher.base.service.project

import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import java.nio.file.Paths

class ProjectServiceTest : ProjectTest() {

    @Autowired
    lateinit var projectService: ProjectService

    @Value("\${catcher.base.local_dir}")
    private val localDir: String? = null

    /**
     * In case local dir is not set (should be the most common case) - it will be placed in projects.
     * All subdirectories should be created.
     */
    @Test
    fun newProjectWithNoLocalPath() {
        // local directory doesn't exist
        val projectDir = Paths.get(localDir, "projects", "test_project").toFile()
        Assert.assertFalse(projectDir.exists())
        val created = projectService.newProject(newProjectDTO("test_project"))
        Assert.assertEquals("", created.remotePath)
        Assert.assertEquals("${Paths.get(localDir).toAbsolutePath()}/projects/test_project", created.localPath)
        // local directories were created
        Assert.assertTrue(projectDir.exists())
        Assert.assertTrue(Paths.get(projectDir.toString(), ProjectScanner.TEST_DIR).toFile().exists())
        Assert.assertTrue(Paths.get(projectDir.toString(), ProjectScanner.STEPS_DIR).toFile().exists())
        Assert.assertTrue(Paths.get(projectDir.toString(), ProjectScanner.RES_DIR).toFile().exists())
    }

    /**
     * In case local dir is specified - it should be used instead
     */
    @Test
    fun newProjectWithLocalPath() {
        val project = createStubProject("test_project")
        // local directory doesn't exist
        val projectDir = Paths.get(localDir, "projects", "test_project").toFile()
        Assert.assertFalse(projectDir.exists())
        val created = projectService.newProject(project.toDTO())
        Assert.assertEquals("", created.remotePath)
        Assert.assertEquals(project.localPath, created.localPath)  // project's local path wasn't changed
        // local directory wasn't created, as project's local path was used
        Assert.assertFalse(projectDir.exists())
    }

    /**
     * Local path shouldn't be changed on update.
     */
    @Test
    fun existingProjectNoLocalPath() {
        val created = projectService.newProject(newProjectDTO( "test_project"))
        Assert.assertEquals("", created.remotePath)
        Assert.assertEquals("${Paths.get(localDir).toAbsolutePath()}/projects/test_project", created.localPath)

        val created2 = projectService.newProject(newProjectDTO("test_project"))
        Assert.assertEquals("", created2.remotePath)
        Assert.assertEquals("${Paths.get(localDir).toAbsolutePath()}/projects/test_project", created2.localPath)
        Assert.assertEquals(created, created2)
    }

    /**
     * Existing local path shouldn't be changed on update
     */
    @Test
    fun existingProjectWithLocalPath() {
        val project = createStubProject("test_project")
        val created = projectService.newProject(project.toDTO())
        Assert.assertEquals(project.localPath, created.localPath)
        val created2 = projectService.newProject(project.toDTO())
        Assert.assertEquals(project.localPath, created2.localPath)
    }
}
package com.catcher.base.service.project

import com.catcher.base.IntegrationTest
import com.catcher.base.data.repository.ProjectRepository
import com.catcher.base.data.repository.TestRepository
import com.catcher.base.service.project.ProjectScanner.Companion.TEST_DIR
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Path
import java.nio.file.Paths

@Transactional
@DirtiesContext
internal class ScanOnRuntimeTest : IntegrationTest() {

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var testsRepository: TestRepository

    @Autowired
    lateinit var projectService: ProjectServiceImpl

    /**
     * New projects should be loaded on application startup
     */
    @Test
    fun testProjectsLoadedOnStartup() {
        newProject("project1", "one.yml", "two.yml", "three.yaml")
        newProject("project2", "testA.json", "testB.JSON")

        projectService.init()

        // projects were created
        val allProjects = projectRepository.findAll().toList()
        Assert.assertEquals(2, allProjects.size)
        Assert.assertEquals("project1", allProjects[0].name)
        Assert.assertEquals("project2", allProjects[1].name)

        // tests were created
        val allTests = testsRepository.findAll().toList()
        Assert.assertEquals(5, allTests.size)

        // all tests are linked to their projects
        val tests1 = allProjects[0].tests.toList().map { it.name }.sorted()
        Assert.assertEquals(3, tests1.size)
        Assert.assertEquals("one.yml", tests1[0])
        Assert.assertEquals("three.yaml", tests1[1])
        Assert.assertEquals("two.yml", tests1[2])

        val tests2 = allProjects[1].tests.toList().map { it.name }.sorted()
        Assert.assertEquals(2, tests2.size)
        Assert.assertEquals("testA.json", tests2[0])
        Assert.assertEquals("testB.JSON", tests2[1])
    }

    /**
     * Existing projects should be updated on application startup
     */
    @Test
    fun testProjectsUpdatedOnStartup() {
        val project1Dir = newProject("project1", "one.yml")
        projectService.init()

        val allProjects = projectRepository.findAll().toList()
        Assert.assertEquals(1, allProjects.size)

        var allTests = testsRepository.findAll().toList()
        Assert.assertEquals(1, allTests.size)

        // additional test was created later
        val testDir = Paths.get(project1Dir.toString(), TEST_DIR).toFile()
        Paths.get(testDir.toString(), "two.yml").toFile().createNewFile()

        // scan runs once again. Update should be indexed.
        projectService.init()

        allTests = testsRepository.findAll().toList()
        Assert.assertEquals(2, allTests.size)

        // both old and new test are linked to project
        val tests = projectRepository.findProjectByName("project1")!!.tests.toList()
        Assert.assertEquals(2, tests.size)
    }

    fun newProject(name: String, vararg tests: String): Path {
        val projects = Paths.get("projects").toAbsolutePath()
        val projectDir = Paths.get(projects.toString(), name)
        projectDir.toFile().mkdirs()
        val testDir = Paths.get(projectDir.toString(), TEST_DIR).toFile()
        testDir.mkdirs()
        tests.forEach { Paths.get(testDir.toString(), it).toFile().createNewFile() }
        return projectDir
    }
}
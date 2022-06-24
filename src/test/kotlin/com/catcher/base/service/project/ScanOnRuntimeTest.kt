package com.catcher.base.service.project

import com.catcher.base.data.repository.ProjectRepository
import com.catcher.base.data.repository.TestRepository
import com.catcher.base.service.project.ProjectScanner.Companion.TEST_DIR
import org.junit.Assert
import org.junit.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.nio.file.Paths

internal class ScanOnRuntimeTest : ProjectTest() {

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
        val log = LoggerFactory.getLogger(this::class.java)
        log.info("--------------------->")
        projectService.projectRepo.findAll().forEach { log.info(it.name) }

        newProject("project1", "one.yml", "two.yml", "three.yaml")
        newProject("project2", "testA.json", "testB.JSON")

        projectService.init()

        // projects were created
        val allProjects = projectRepository.findAll().toList().sortedBy { it.name }
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

        val log = LoggerFactory.getLogger(this::class.java)
        log.info("--------------------->")
        projectService.projectRepo.findAll().forEach { log.info(it.name) }

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
}
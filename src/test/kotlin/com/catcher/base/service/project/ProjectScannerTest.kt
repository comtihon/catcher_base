package com.catcher.base.service.project

import com.catcher.base.IntegrationTest
import com.catcher.base.data.dao.Project
import com.catcher.base.data.repository.ProjectRepository
import com.catcher.base.data.repository.TestRepository
import com.catcher.base.service.project.ProjectScanner.Companion.TEST_DIR
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import java.nio.file.Path
import java.nio.file.Paths


internal class ProjectScannerTest : IntegrationTest() {

    @Autowired
    lateinit var scanner: ProjectScanner

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var testRepository: TestRepository

    val testProjectDir = Paths.get(java.io.File(".").canonicalPath,
            "src",
            "test",
            "resources",
            this.javaClass.name)!!

    @After
    override fun tearDown() {
        super.tearDown()
        testProjectDir.toFile().deleteRecursively()
    }

    /**
     * Just a simple scan
     */
    @Test
    fun scanSimple() {
        val project = createStubProject()
        projectRepository.save(project)
        val testDir = Paths.get(project.localPath, TEST_DIR).toString()
        addTest(Paths.get(testDir, "foo.yaml"))
        addTest(Paths.get(testDir, "bar.yaml"))
        addTest(Paths.get(testDir, "baz.yaml"))
        scanner.indexProject(project)
        assertEquals(3, project.tests.size)
        // tests were attached to project
        val tests = project.tests.toList().sortedBy { it.name }
        assertEquals("bar.yaml", tests[0].name)
        assertEquals("baz.yaml", tests[1].name)
        assertEquals("foo.yaml", tests[2].name)
        // tests were saved to db
        val allTests = testRepository.findAll().sortedBy { it.name }
        assertEquals(3, allTests.size)
        assertEquals("bar.yaml", allTests[0].name)
        assertEquals("baz.yaml", allTests[1].name)
        assertEquals("foo.yaml", allTests[2].name)
        // project <-> tests mapping was saved
        val found = projectRepository.findByIdOrNull(project.id)
        assertNotNull(found)
        assertEquals(project.tests, found!!.tests)
    }

    /**
     * Simple scan test variation with tests in subdirs
     */
    @Test
    fun scanSimpleSubdirs() {
        val project = createStubProject()
        projectRepository.save(project)
        val testDir = Paths.get(project.localPath, TEST_DIR).toString()
        addTest(Paths.get(testDir, "foo.yaml"))
        addTest(Paths.get(testDir, "subdir1", "bar.yaml"))
        addTest(Paths.get(testDir, "subdir1", "baz.yaml"))
        addTest(Paths.get(testDir, "subdir1", "subdir2", "far.json"))
        scanner.indexProject(project)
        assertEquals(4, project.tests.size)
        // all tests were attached to project
        val tests = project.tests.toList().sortedBy { it.name }
        assertEquals("bar.yaml", tests[0].name)
        assertEquals("baz.yaml", tests[1].name)
        assertEquals("far.json", tests[2].name)
        assertEquals("foo.yaml", tests[3].name)
    }

    /**
     * Project was scanned once. Some tests were added since the last scan.
     */
    @Test
    fun scanNewTests() {
        val project = createStubProject()
        projectRepository.save(project)
        val testDir = Paths.get(project.localPath, TEST_DIR).toString()
        addTest(Paths.get(testDir, "foo.yaml"))
        scanner.indexProject(project)
        assertEquals(1, project.tests.size)
        assertEquals("foo.yaml", project.tests.toList()[0].name)

        addTest(Paths.get(testDir, "bar.yaml"))
        addTest(Paths.get(testDir, "subdir1", "subdir2", "far.json"))
        scanner.indexProject(project)
        // new tests were attached
        assertEquals(3, project.tests.size)
        val tests = project.tests.toList().sortedBy { it.name }
        assertEquals("bar.yaml", tests[0].name)
        assertEquals("far.json", tests[1].name)
        assertEquals("foo.yaml", tests[2].name)
    }


    /**
     * Project was scanned before. Some tests were deleted after.
     */
    @Test
    fun removeOldTests() {
        val project = createStubProject()
        projectRepository.save(project)
        val testDir = Paths.get(project.localPath, TEST_DIR).toString()
        addTest(Paths.get(testDir, "foo.yaml"))
        addTest(Paths.get(testDir, "bar.yaml"))
        addTest(Paths.get(testDir, "baz.yaml"))
        scanner.indexProject(project)
        assertEquals(3, project.tests.size)
        Paths.get(testDir, "bar.yaml").toFile().delete()
        scanner.indexProject(project)
        // test was removed from project
        assertEquals(2, project.tests.size)
    }

    /**
     * Test non-supported files are filtered
     */
    @Test
    fun filterOtherFiles() {
        val project = createStubProject()
        projectRepository.save(project)
        val testDir = Paths.get(project.localPath, TEST_DIR).toString()
        addTest(Paths.get(testDir, "foo.json"))
        addTest(Paths.get(testDir, "bar.py"))
        addTest(Paths.get(testDir, "baz.yaml"))
        scanner.indexProject(project)
        assertEquals(2, project.tests.size)
        val tests = project.tests.toList().sortedBy { it.name }
        assertEquals("baz.yaml", tests[0].name)
        assertEquals("foo.json", tests[1].name)
    }

    private fun createStubProject(): Project {
        val projectDir = Paths.get(testProjectDir.toString(), "testProject")
        projectDir.toFile().mkdirs()
        val testDir = Paths.get(projectDir.toString(), TEST_DIR)
        testDir.toFile().mkdirs()
        return Project(0,
                "testProject",
                projectDir.toString(),
                null,
                mutableSetOf(),
                mutableSetOf())
    }

    private fun addTest(testPath: Path) {
        testPath.parent.toFile().mkdirs()
        testPath.toFile().createNewFile()
    }
}
package com.catcher.base.service.project

import com.catcher.base.FunctionalTest
import org.junit.After
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Path
import java.nio.file.Paths

@Transactional
@DirtiesContext
abstract class ProjectTest : FunctionalTest() {

    @After
    override fun tearDown() {
        super.tearDown()
        testProjectDir.toFile().deleteRecursively()
    }

    /**
     * Create project in server's projects directory. Add some tests (empty files)
     */
    fun newProject(name: String, vararg tests: String): Path {
        val projects = Paths.get("projects").toAbsolutePath()
        val projectDir = Paths.get(projects.toString(), name)
        projectDir.toFile().mkdirs()
        val testDir = Paths.get(projectDir.toString(), ProjectScanner.TEST_DIR).toFile()
        testDir.mkdirs()
        tests.forEach { Paths.get(testDir.toString(), it).toFile().createNewFile() }
        return projectDir
    }
}
package com.catcher.base.service.project

import com.catcher.base.data.entity.Project
import com.catcher.base.data.entity.Test
import com.catcher.base.data.repository.TestRepository
import com.catcher.base.service.project.fetcher.FetcherFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

@Component
class ProjectScanner(
    @Autowired val testRepository: TestRepository,
    @Autowired val fetcherFactory: FetcherFactory
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * Fetch project if it is new or update.
     * Indexes it.
     */
    fun scanProject(project: Project, isNew: Boolean = true) {
        if (project.remotePath == null) {
            log.info("No remote for ${project.name}")
        } else {
            val fetcher = fetcherFactory.getProjectFetcher(project.remotePath!!)
            if (fetcher == null) {
                log.warn("No suitable fetcher for remote ${project.remotePath}")
                // TODO should I notify end user about it?
            }

            with(project) {
                when {
                    isNew -> fetcher?.fetchRemote(remotePath!!, localPath)
                    else -> fetcher?.updateRemote(remotePath!!, localPath)
                }

            }
        }
        indexProject(project)
    }


    /**
     * Index all tests inside project
     */
    fun indexProject(project: Project) {
        val testPath = Paths.get(project.localPath, TEST_DIR)
        if (!testPath.toFile().exists()) {
            log.warn("No such file: $testPath")
            throw Exception("Project's local dir is corrupted.")
        }
        val tests: MutableSet<Path> = mutableSetOf()
        indexPath(Files.list(testPath), tests)
        // add only new tests
        val names = project.tests.map { it.path().toString() }
        tests.filter { !names.contains(it.toString()) }
            .map {
                val rel = testPath.relativize(it)
                var testDir = ""
                if(rel != rel.fileName)
                    testDir = rel.parent.toString()
                testRepository.save(
                    Test(
                        name = it.fileName.toString(),
                        path = testDir,
                        project = project
                    )
                )
            }
            .forEach { project.tests.add(it) }
        // remove old tests (deleted or renamed in project's dir)
        val testsStr = tests.map { it.toString() }
        project.tests.removeIf { !testsStr.contains(it.path().toString()) }
        // TODO remove tests from database (and history)?
    }

    /**
     * find every test (yml, yaml or json)
     * add test to tests
     * for every subdir repeat 1-2
     */
    private fun indexPath(files: Stream<Path>, tests: MutableSet<Path>) {
        files.forEach {
            val file = it.toFile()
            if (file.isFile && (file.extension.toLowerCase() == "yaml"
                        || file.extension.toLowerCase() == "yml"
                        || file.extension.toLowerCase() == "json")
            ) {
                tests.add(it)
            } else if (file.isDirectory) {
                indexPath(Files.list(it), tests)
            }
        }
    }

    companion object {
        const val TEST_DIR = "tests"
        const val STEPS_DIR = "steps"
        const val RES_DIR = "res"
    }
}
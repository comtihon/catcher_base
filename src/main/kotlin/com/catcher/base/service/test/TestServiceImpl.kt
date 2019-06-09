package com.catcher.base.service.test

import com.catcher.base.data.dao.RunStatus
import com.catcher.base.data.dao.Test
import com.catcher.base.data.dao.TestRun
import com.catcher.base.data.dto.TestDTO
import com.catcher.base.data.dto.TestRunDTO
import com.catcher.base.data.repository.ProjectRepository
import com.catcher.base.data.repository.TestRepository
import com.catcher.base.data.repository.TestRunRepository
import com.catcher.base.exception.*
import com.catcher.base.service.project.ProjectScanner
import com.catcher.base.service.test.runner.TestRunner
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.CompletableFuture
import javax.annotation.PostConstruct

@Service
class TestServiceImpl(@Autowired val projectRepo: ProjectRepository,
                      @Autowired val testRepository: TestRepository,
                      @Autowired val testRunner: TestRunner,
                      @Autowired val runFactory: ObjectProvider<TestRun>,
                      @Autowired val testRunRepository: TestRunRepository) : TestService {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * Creates test in a file system
     */
    @Transactional
    override fun newTest(projectId: Int, test: TestDTO) {
        if (test.data == null && test.path == null)
            throw NoTestContentException()
        val project = projectRepo.findByIdOrNull(projectId) ?: throw ProjectNotFoundException()
        if (test.path == null) {  // TODO test storage (local FS/FTP/Database/git)?
            test.path = createFile(Paths.get(project.localPath, ProjectScanner.TEST_DIR, test.name), test.data!!)
        }
        val savedTest = testRepository.save(Test(0,
                test.name,
                test.path!!,
                mutableSetOf(),
                project))
        project.tests.add(savedTest)
    }

    override fun updateTest(test: TestDTO) {
        if (test.id == null)
            throw ParamRequiredException("Param id is required")
        if (test.data == null)
            throw ParamRequiredException("Param data is required")
        val found = testRepository.findByIdOrNull(test.id) ?: throw FileNotFoundException("No such test")
        updateFile(Paths.get(found.path), test.data!!)
    }

    @Transactional
    override fun deleteTest(projectId: Int, testId: Int) {
        val test = testRepository.findByIdOrNull(testId) ?: throw FileNotFoundException("No such test")
        val project = projectRepo.findByIdOrNull(projectId) ?: throw ProjectNotFoundException()
        if (!Paths.get(test.path).toFile().delete())
            log.warn("Fail to delete ${test.path}")  // TODO inform user?
        project.tests.remove(test)
        testRepository.delete(test)
    }

    override fun contentForTest(testId: Int): TestDTO {
        val test = testRepository.findByIdOrNull(testId) ?: throw FileNotFoundException("No such test")
        val content = Paths.get(test.path).toFile().readText()
        return TestDTO(test.id, test.name, test.path, content)
    }

    override fun runTest(testId: Int): CompletableFuture<TestRunDTO> {
        val test = testRepository.findByIdOrNull(testId) ?: throw FileNotFoundException("No such test")
        val testRun = runFactory.getObject(test)
        testRunRepository.save(testRun)
        return testRunner.runTest(testRun).thenApply { it.toDTO() }
    }

    override fun status(testRunId: Int): TestRunDTO? {
        return testRunRepository.findByIdOrNull(testRunId)?.toDTO()
    }

    @PostConstruct
    fun init() {
        // resume all tests, that were running before
        for (run in testRunRepository.getNotFinished()) {
            run.status = RunStatus.QUEUED
            testRunRepository.save(run)
            testRunner.runTest(run)
        }
    }

    private fun createFile(fileName: Path, content: String): String {
        val fileToWrite = fileName.toFile()
        if (!fileToWrite.exists())
            fileToWrite.writeText(content)
        else
            throw FileException("File $fileName already exists.")
        return fileName.toString()
    }

    private fun updateFile(fileName: Path, content: String) {
        val fileToWrite = fileName.toFile()
        if (!fileToWrite.exists())
            throw FileException("File $fileName doesn't exist.")
        else {
            fileToWrite.writeText(content)
        }
    }
}
package com.catcher.base.service.test

import com.catcher.base.data.dao.Test
import com.catcher.base.data.dto.TestDTO
import com.catcher.base.data.dto.TestRunDTO
import com.catcher.base.data.repository.ProjectRepository
import com.catcher.base.data.repository.TestRepository
import com.catcher.base.exception.*
import com.catcher.base.service.project.ProjectScanner
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Path
import java.nio.file.Paths

@Service
class TestServiceImpl(@Autowired val projectRepo: ProjectRepository,
                      @Autowired val testRepository: TestRepository) : TestService {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * Creates test in a file system
     */
    @Transactional
    override fun newTest(projectId: Int, test: TestDTO) {
        if (test.data == null && test.path == null)
            throw NoTestContentException()
        val project = projectRepo.findByIdOrNull(projectId) ?: throw ProjectNotFoundException()
        if (test.path == null) {
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

    override fun runTest(testId: Int): TestRunDTO {
        //TODO find test to run
        //TODO create TestRun, save to database and put it in the run queue.
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun status(testRunId: Int): TestRunDTO {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
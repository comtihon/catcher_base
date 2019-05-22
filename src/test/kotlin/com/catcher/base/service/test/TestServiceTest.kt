package com.catcher.base.service.test

import com.catcher.base.FunctionalTest
import com.catcher.base.data.dao.Project
import com.catcher.base.data.dao.RunStatus
import com.catcher.base.data.dao.TestRun
import com.catcher.base.data.repository.ProjectRepository
import com.catcher.base.data.repository.TestRepository
import com.catcher.base.data.repository.TestRunRepository
import com.catcher.base.service.project.ProjectScanner
import com.catcher.base.service.tool.Catcher
import com.catcher.base.service.tool.system.SystemTool
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

internal class TestServiceTest : FunctionalTest() {

    @Autowired
    lateinit var testRepository: TestRepository

    @Autowired
    lateinit var testRunRepository: TestRunRepository

    @Autowired
    lateinit var testService: TestService

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var systemTool: SystemTool

    @Autowired
    lateinit var catcher: Catcher

    @Autowired
    lateinit var runFactory: ObjectProvider<TestRun>

    @Autowired
    lateinit var testExecutor: ThreadPoolTaskExecutor

    override fun setUp() {
        super.setUp()
        systemTool.install()
        catcher.install()
    }

    @Test
    fun testRunSuccessful() {
        val project = createStubProject()
        projectRepository.save(project)
        val test = newTestOK(project, "foo.yaml")
        val runFuture = testService.runTest(test.id)
        val runDTO = runFuture.get()
        Assert.assertEquals(RunStatus.FINISHED, runDTO.status)
        Assert.assertNotNull(runDTO.finished)
        val dbRun = testRunRepository.findByIdOrNull(runDTO.id)
        Assert.assertNotNull(dbRun)
        Assert.assertNotNull(dbRun!!.finished)
        Assert.assertEquals(RunStatus.FINISHED, dbRun.status)
        Assert.assertNotNull(dbRun.output)
    }

    @Test
    fun testRunErrored() {
        val project = createStubProject()
        projectRepository.save(project)
        val test = newTestFail(project, "foo.yaml")
        val runFuture = testService.runTest(test.id)
        val runDTO = runFuture.get()
        Assert.assertEquals(RunStatus.FAILED, runDTO.status)
        Assert.assertNotNull(runDTO.finished)
        val dbRun = testRunRepository.findByIdOrNull(runDTO.id)
        Assert.assertNotNull(dbRun)
        Assert.assertNotNull(dbRun!!.finished)
        Assert.assertEquals(RunStatus.FAILED, dbRun.status)
        Assert.assertNotNull(dbRun.output)
    }

    @Test
    fun testRerunOnStartup() {
        val project = createStubProject()
        projectRepository.save(project)
        val test1 = newTestOK(project, "foo.yaml")
        val test2 = newTestOK(project, "bar.yaml")
        // 1 queued, 1 running, 1 finished
        val run1 = runFactory.getObject(test1)
        val run2 = runFactory.getObject(test2)
        val run3 = runFactory.getObject(test2)
        run2.status = RunStatus.STARTED
        run3.status = RunStatus.FINISHED
        run3.finished = LocalDateTime.now().minusDays(2)
        testRunRepository.saveAll(listOf(run1, run2, run3))
        // 2 unfinished
        var notFinished = testRunRepository.getNotFinished()
        Assert.assertEquals(2, notFinished.size)
        Assert.assertTrue(notFinished.contains(run1))
        Assert.assertTrue(notFinished.contains(run2))
        Assert.assertFalse(notFinished.contains(run3))
        // call init "at startup"
        (testService as TestServiceImpl).init()
        testExecutor.threadPoolExecutor.awaitTermination(3, TimeUnit.SECONDS)
        // no unfinished
        notFinished = testRunRepository.getNotFinished()
        Assert.assertTrue(notFinished.isEmpty())
        // statuses were changed
        val dbRun1 = testRunRepository.findByIdOrNull(run1.id)
        Assert.assertEquals(RunStatus.FINISHED, dbRun1!!.status)
        val dbRun2 = testRunRepository.findByIdOrNull(run2.id)
        Assert.assertEquals(RunStatus.FINISHED, dbRun2!!.status)
    }

    private fun newTestOK(project: Project, name: String): com.catcher.base.data.dao.Test {
        return newTest(project, name, """---
                          steps:
                             - echo: {from: 'hello', register: {'foo': 'value'}}
                             - check: {equals: {the: '{{ foo }}', is: 'value'}}
                          """.trimIndent())
    }

    private fun newTestFail(project: Project, name: String): com.catcher.base.data.dao.Test {
        return newTest(project, name, """---
                          steps:
                             - echo: {from: 'hello', register: {'foo': 'value'}}
                             - check: {equals: {the: '{{ foo }}', is: 'error'}}
                          """.trimIndent())
    }

    private fun newTest(project: Project, name: String, content: String): com.catcher.base.data.dao.Test {
        val testDir = Paths.get(project.localPath, ProjectScanner.TEST_DIR).toString()
        addTest(Paths.get(testDir, name), content = content)
        return testRepository.save(com.catcher.base.data.dao.Test(0,
                name,
                Paths.get(testDir, name).toString(),
                mutableSetOf(),
                project))
    }

    // TODO test concurrent execution (override default coreSize from config)
}
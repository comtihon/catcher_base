package com.catcher.base.service.test.runner

import com.catcher.base.data.entity.RunStatus
import com.catcher.base.data.entity.TestRun
import com.catcher.base.data.repository.TestRunRepository
import com.catcher.base.exception.ExecutionFailedException
import com.catcher.base.service.tool.Catcher
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Service
class LocalTestRunner(@Autowired val testRunRepository: TestRunRepository,
                      @Autowired val catcher: Catcher) : TestRunner {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Async("testExecutor")
    override fun runTest(test: TestRun): CompletableFuture<TestRun> {
        return CompletableFuture.completedFuture(execute(test))
    }

    private fun execute(test: TestRun): TestRun {
        log.info("Run ${test.test.name}")
        test.status = RunStatus.STARTED
        test.started = LocalDateTime.now()
        testRunRepository.save(test)
        try {
            test.output = catcher.runTest(test.test.path().toString())
            test.status = RunStatus.FINISHED
        } catch (e: ExecutionFailedException) {
            test.output = e.localizedMessage
            test.status = RunStatus.FAILED
        }
        test.finished = LocalDateTime.now()
        return testRunRepository.save(test)
    }
}
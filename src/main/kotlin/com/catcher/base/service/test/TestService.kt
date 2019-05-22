package com.catcher.base.service.test

import com.catcher.base.data.dto.TestDTO
import com.catcher.base.data.dto.TestRunDTO
import java.util.concurrent.CompletableFuture

interface TestService {
    fun newTest(projectId: Int, test: TestDTO)

    fun updateTest(test: TestDTO)

    fun contentForTest(testId: Int): TestDTO

    fun deleteTest(projectId: Int, testId: Int)

    fun runTest(testId: Int): CompletableFuture<TestRunDTO>

    fun status(testRunId: Int): TestRunDTO?
}
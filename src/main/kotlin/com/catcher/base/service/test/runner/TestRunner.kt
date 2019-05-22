package com.catcher.base.service.test.runner

import com.catcher.base.data.dao.TestRun
import java.util.concurrent.CompletableFuture

interface TestRunner {
    fun runTest(test: TestRun): CompletableFuture<TestRun>
}
package com.catcher.base.config

import com.catcher.base.data.entity.Test
import com.catcher.base.data.entity.TestRun
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor


@Configuration
class ServerConfig {

    @Value("\${catcher.executor.local.pool.max_size:}")
    private val maxSize: Int? = null

    @Value("\${catcher.executor.local.pool.core_size:}")
    private val coreSize: Int? = null

    // TODO move me to LocalTestConfiguration and make it autoconfiguration based on config test_executor.local
    @Bean(name = ["testExecutor"])
    fun threadPoolTaskExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.maxPoolSize = maxSize ?: Runtime.getRuntime().availableProcessors()
        executor.corePoolSize = coreSize ?: 1
        executor.setThreadNamePrefix("TestRunner-")
        executor.initialize()
        return executor
    }

    @Bean
    @Scope("prototype")
    fun testRun(test: Test): TestRun {
        return TestRun(test)
    }
}
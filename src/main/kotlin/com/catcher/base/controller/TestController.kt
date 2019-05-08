package com.catcher.base.controller

import com.catcher.base.data.dto.TestDTO
import com.catcher.base.data.dto.TestRunDTO
import com.catcher.base.service.test.TestService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

// TODO do not forget to add hasAuthority to SecurityConfig!
@RestController
@RequestMapping("/api/v1/test")
class TestController(@Autowired val testService: TestService) {
    // for create & delete test see ProjectController

    @PutMapping
    fun update(@Valid @RequestBody test: TestDTO) {
        testService.updateTest(test)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Int): TestDTO {
        return testService.contentForTest(id)
    }

    // TODO async
    @GetMapping("/run/{id}")
    fun run(@PathVariable id: Int): TestRunDTO {
        return testService.runTest(id)
    }

    @GetMapping("/log/{id}")
    fun log(@PathVariable id: Int): TestRunDTO {
        return testService.status(id)
    }
}
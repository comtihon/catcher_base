package com.catcher.base.data.repository

import com.catcher.base.data.dao.TestRun
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TestRunRepository : CrudRepository<TestRun, Int> {
    @Query("SELECT r FROM TestRun r WHERE r.status = 'QUEUED' or r.status = 'STARTED'")
    fun getNotFinished(): List<TestRun>
}
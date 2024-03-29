package com.catcher.base.data.repository

import com.catcher.base.data.entity.Test
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TestRepository : CrudRepository<Test,Int>
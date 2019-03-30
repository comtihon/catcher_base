package com.catcher.base.data.repository

import com.catcher.base.data.dao.Project
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : CrudRepository<Project, Int> {
    @Query("SELECT p FROM Project p JOIN Team t JOIN User u WHERE u.email = ?#{ principal?.username }")
    fun getAllForCurrentUser(): List<Project>  // TODO testme (see generated query)

    fun findProjectByName(name: String): Project?
}
package com.catcher.base.data.repository

import com.catcher.base.data.dao.Project
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : CrudRepository<Project, Int> {
    @Query("SELECT p FROM Project p JOIN p.teams t JOIN t.users u WHERE u.email = ?#{ principal }")
    fun getAllForCurrentUser(): List<Project>

    fun findProjectByName(name: String): Project?

    @Query("SELECT p FROM Project p LEFT JOIN p.tests t LEFT JOIN t.runs tr WHERE p.id =:id")
    fun findByIdWithTests(@Param("id") id: Int): Project?
}
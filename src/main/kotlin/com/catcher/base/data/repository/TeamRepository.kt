package com.catcher.base.data.repository

import com.catcher.base.data.dao.Team
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamRepository : CrudRepository<Team, String> {
    @Query("SELECT t from Team  t JOIN t.users u WHERE u.email = ?#{ principal }")
    fun getAllForCurrentUser(): List<Team>
}
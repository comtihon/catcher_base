package com.catcher.base.data.repository

import com.catcher.base.data.dao.Team
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamRepository : CrudRepository<Team, String>
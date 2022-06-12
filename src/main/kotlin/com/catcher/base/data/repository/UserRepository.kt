package com.catcher.base.data.repository

import com.catcher.base.data.entity.User
import org.springframework.data.repository.CrudRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import javax.transaction.Transactional

@Repository
interface UserRepository : CrudRepository<User, String> {
    @Query("UPDATE User u SET u.lastLogin=:lastLogin WHERE u.email = ?#{ principal }")
    @Modifying
    @Transactional
    fun updateLastLogin(@Param("lastLogin") lastLogin: LocalDateTime)

    @Query("SELECT u FROM User u WHERE u.role.name = 'admin'")
    fun findAdmins(): List<User>
}
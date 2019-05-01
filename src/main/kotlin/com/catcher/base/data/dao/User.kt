package com.catcher.base.data.dao

import com.catcher.base.data.dto.UserDTO
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(@Id val email: String,
                val name: String,
                @Column(length = 60)
                val phash: String,
                val lastLogin: LocalDateTime?,
                @ManyToMany(mappedBy = "users")
                val teams: Set<Team>,
                @ManyToOne
                val role: Role) {
    fun toDTO(): UserDTO {
        return UserDTO(email, null, name, role.name)
    }

    override fun toString(): String {
        return "User(email='$email', name='$name', phash='$phash', lastLogin=$lastLogin, role=$role)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }


}
package com.catcher.base.data.dto

import com.catcher.base.data.dao.Role
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class RoleDTO(val id: Int?,
                   @NotEmpty @NotNull
                   val name: String,
                   val privileges: List<String>) {
    fun toDAO(): Role {
        return Role(0, name, mutableSetOf(), mutableSetOf())
    }
}
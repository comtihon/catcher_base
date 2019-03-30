package com.catcher.base.data.dto

import com.catcher.base.data.dao.Team
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class TeamDTO(@NotEmpty @NotNull
                   val name: String) {
    fun toDAO(): Team {
        return Team(name, mutableSetOf(), mutableSetOf())
    }
}
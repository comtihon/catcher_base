package com.catcher.base.data.dto

import com.catcher.base.data.entity.Team
import com.catcher.base.data.dto.validation.ValidTeamName
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class TeamDTO(@NotEmpty @NotNull @ValidTeamName
                   val name: String,
                   val users: List<UserDTO>) {
    fun toEntity() = Team(name, mutableSetOf(), mutableSetOf())
}
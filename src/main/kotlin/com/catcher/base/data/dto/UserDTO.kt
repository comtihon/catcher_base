package com.catcher.base.data.dto

import com.catcher.base.data.dto.validation.ValidEmail
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class UserDTO(@ValidEmail @NotEmpty @NotNull
                   val email: String,
                   val password: String?,
                   val name: String?,
                   val role: List<String>)
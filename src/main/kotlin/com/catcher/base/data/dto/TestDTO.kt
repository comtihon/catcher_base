package com.catcher.base.data.dto

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class TestDTO(val id: Int?,
                   @NotEmpty @NotNull
                   val name: String,
                   var path: String?,
                   var data: String?,
                   var lastRun: TestRunDTO?)
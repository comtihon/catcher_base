package com.catcher.base.data.dto

import com.catcher.base.data.dao.RunStatus
import java.time.LocalDateTime

data class TestRunDTO(val id: Int,
                      val status: RunStatus,
                      val started: LocalDateTime?,
                      val finished: LocalDateTime?,
                      val output: String?)
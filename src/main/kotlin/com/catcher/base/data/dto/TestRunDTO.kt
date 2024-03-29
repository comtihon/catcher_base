package com.catcher.base.data.dto

import com.catcher.base.data.entity.RunStatus
import java.time.LocalDateTime

data class TestRunDTO(val id: Int,
                      val status: RunStatus,
                      val queued: LocalDateTime?,
                      val started: LocalDateTime?,
                      val finished: LocalDateTime?,
                      val output: String?)
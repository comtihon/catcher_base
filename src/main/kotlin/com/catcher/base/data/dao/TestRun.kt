package com.catcher.base.data.dao

import java.time.LocalDateTime
import javax.persistence.*

enum class RunStatus {
    STARTED, FAILED, FINISHED, ABORTED
}

@Entity
@Table(name = "runs")
data class TestRun(@Id @GeneratedValue(strategy = GenerationType.AUTO)
                   val id: Int,
                   val testId: Int,
                   @Enumerated(EnumType.STRING)
                   val status: RunStatus,
                   val started: LocalDateTime,
                   val finished: LocalDateTime,
                   val output: String) {
}
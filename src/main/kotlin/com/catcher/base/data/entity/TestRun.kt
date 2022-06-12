package com.catcher.base.data.entity

import com.catcher.base.data.dto.TestRunDTO
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.*

enum class RunStatus {
    QUEUED, STARTED, FAILED, FINISHED, ABORTED
}

@Entity
@Table(name = "runs")
data class TestRun(@Id @GeneratedValue(strategy = GenerationType.AUTO)
                   val id: Int,
                   @Enumerated(EnumType.STRING)
                   var status: RunStatus,
                   @CreationTimestamp
                   var queued: LocalDateTime?,
                   var started: LocalDateTime?,
                   var finished: LocalDateTime?,
                   @Column(length = 10485760)
                   var output: String?,
                   @ManyToOne
                   val test: Test) {
    constructor(test: Test) : this(0, RunStatus.QUEUED, null, null, null, null, test)

    fun toDTO() = TestRunDTO(id, status, queued, started, finished, output)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestRun

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "TestRun(id=$id, status=$status, started=$started, finished=$finished)"
    }
}
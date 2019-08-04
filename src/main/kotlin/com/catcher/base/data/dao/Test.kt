package com.catcher.base.data.dao

import com.catcher.base.data.dto.TestDTO
import org.hibernate.annotations.JoinFormula
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "tests")
data class Test(@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
                val id: Int,
                val name: String,
                val path: String,  // TODO store test content in database instead of FS?
                @OneToMany(cascade = [CascadeType.REMOVE], mappedBy = "test")
                val runs: MutableSet<TestRun>,
                @ManyToOne(fetch = FetchType.EAGER)
                @JoinFormula("(" +
                        "SELECT tr.id from runs tr " +
                        "WHERE tr.test_id = id " +
                        "ORDER BY tr.started DESC " +
                        "LIMIT 1 " +
                        ")")
                val lastRun: TestRun?,
                @ManyToOne
                val project: Project,
                @UpdateTimestamp
                val updatedAt: LocalDateTime?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Test

        if (id != other.id) return false
        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + path.hashCode()
        return result
    }

    override fun toString(): String {
        return "Test(id=$id, name='$name', path='$path')"
    }

    fun toDTO(): TestDTO = TestDTO(id, name, path, null, lastRun?.toDTO(), updatedAt, null)

    /**
     * Includes all runs information
     */
    fun toFullDTO(): TestDTO = TestDTO(id, name, path, null, lastRun?.toDTO(), updatedAt, runs.map(TestRun::toDTO))
}
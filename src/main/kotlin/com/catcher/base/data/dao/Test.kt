package com.catcher.base.data.dao

import com.catcher.base.data.dto.TestDTO
import org.hibernate.annotations.JoinFormula
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
                val project: Project) {
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

    fun toDTO(): TestDTO {
        return TestDTO(id, name, path, null, lastRun?.toDTO())
    }
}
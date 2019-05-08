package com.catcher.base.data.dao

import com.catcher.base.data.dto.TestDTO
import javax.persistence.*

@Entity
@Table(name = "tests")
data class Test(@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
                val id: Int,
                val name: String,
                val path: String,
                @OneToMany(cascade = [CascadeType.REMOVE])
                val runs: MutableSet<TestRun>,
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
        return TestDTO(id, name, path, null) // TODO data
    }
}
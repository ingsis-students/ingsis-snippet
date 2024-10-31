package com.students.ingsissnippet.repositories

import com.students.ingsissnippet.entities.Test
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestRepository : JpaRepository<Test, Long> {
    fun findBySnippetId(snippetId: Long): List<Test>
}

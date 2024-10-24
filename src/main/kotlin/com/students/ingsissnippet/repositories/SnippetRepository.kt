package com.students.ingsissnippet.repositories

import com.students.ingsissnippet.entities.Snippet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SnippetRepository : JpaRepository<Snippet, Long> {
    fun findByOwner(id: Long): List<Snippet>
}

package com.students.ingsissnippet.repositories

import com.students.ingsissnippet.entities.Snippet
import org.springframework.stereotype.Repository

@Repository
class SnippetRepository {
    fun createSnippet(name: String, content: String, language: String): Snippet {
        // Create Snippet
        // Add to db
        // Finally return the snippet
        return Snippet(1, name, content, language, emptyList(), "owner", emptyList())
    }

    fun getSnippetOfId(id: Long): Snippet {
        // Get Snippet from db
        return Snippet(1, "name", "content", "language", emptyList(), "owner", emptyList())
    }
}
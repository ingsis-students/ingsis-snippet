package com.students.ingsissnippet.services

import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.repositories.SnippetRepository
import org.springframework.stereotype.Service

@Service
class SnippetService {
    private val snippetRepository = SnippetRepository()

    fun createSnippet(name: String, content: String, language: String): Snippet {
        return snippetRepository.createSnippet(name, content, language)
    }

    fun getSnippetOfId(id: Long): Snippet {
        return snippetRepository.getSnippetOfId(id)
    }
}
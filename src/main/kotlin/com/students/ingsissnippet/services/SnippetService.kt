package com.students.ingsissnippet.services

import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.repositories.SnippetRepository
import org.springframework.stereotype.Service

@Service
class SnippetService(private val snippetRepository: SnippetRepository) {

    fun createSnippet(name: String, content: String, language: String): Snippet {
        val snippet =
            Snippet(name = name, content = content, language = language, owner = "admin", guests = emptyList())
        snippetRepository.save(snippet)
        return snippet
    }

    fun getSnippetOfId(id: Long): Snippet {
        return snippetRepository.findById(id).orElseThrow { NoSuchElementException("Snippet not found") }
    }

    fun editSnippet(id: Long, content: String): Snippet? {
        val snippetOptional = snippetRepository.findById(id)
        return if (snippetOptional.isPresent) {
            val snippet = snippetOptional.get()
            val updatedSnippet = snippet.copy(content = content)
            snippetRepository.save(updatedSnippet)
            updatedSnippet
        } else {
            throw NoSuchElementException("Snippet not found when trying to edit it")
        }
    }
}

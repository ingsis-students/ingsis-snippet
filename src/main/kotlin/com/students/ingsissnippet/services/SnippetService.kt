package com.students.ingsissnippet.services

import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.errors.SnippetNotFound
import com.students.ingsissnippet.repositories.SnippetRepository
import com.students.ingsissnippet.routes.SnippetServiceRoutes
import org.springframework.stereotype.Service

@Service
class SnippetService(
    private val snippetRepository: SnippetRepository,
    private val permissionService: PermissionService
) : SnippetServiceRoutes {

    override fun createSnippet(name: String, content: String, language: String, owner: String): Snippet {
        val snippet = Snippet(name = name, content = content, language = language, owner = owner)
        snippetRepository.save(snippet)
        permissionService.addSnippetToUser(owner, snippet.id, "Owner")
        return snippet
    }

    override fun getSnippetOfId(id: Long): Snippet {
        return snippetRepository.findById(id).orElseThrow { NoSuchElementException("Snippet not found") }
    }

    override fun editSnippet(id: Long, content: String): Snippet? {
        checkIfExists(id, "edit")
        val snippet = snippetRepository.findById(id).get()
        val updatedSnippet = snippet.copy(content = content)
        snippetRepository.save(updatedSnippet)
        return updatedSnippet
    }

    override fun deleteSnippet(id: Long) {
        checkIfExists(id, "delete")
        snippetRepository.deleteById(id)
    }

    override fun checkIfExists(id: Long, operation: String) {
        if (!snippetRepository.existsById(id)) {
            throw SnippetNotFound("Snippet not found when trying to $operation it")
        }
    }

    override fun getByUser(id: Long): List<Snippet> {
        return snippetRepository.findByOwner(id)

    }
}

package com.students.ingsissnippet.services

import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.dtos.response_dtos.FullSnippet
import com.students.ingsissnippet.entities.Language
import com.students.ingsissnippet.errors.SnippetNotFound
import com.students.ingsissnippet.repositories.SnippetRepository
import com.students.ingsissnippet.routes.SnippetServiceRoutes
import org.springframework.stereotype.Service

@Service
class SnippetService(
    private val snippetRepository: SnippetRepository,
    private val permissionService: PermissionService,
    private val assetService: AssetService
) : SnippetServiceRoutes {

    override fun create(name: String, content: String, language: Language, owner: String, token: String): FullSnippet {
        val snippet = Snippet(name = name, language = language, owner = owner)
        snippetRepository.save(snippet)
        assetService.put("snippets", snippet.id, content)
        permissionService.addSnippetToUser(token, owner, snippet.id, "Owner")
        return FullSnippet(snippet, content)
    }

    override fun get(id: Long): FullSnippet {
        val snippet = snippetRepository.findById(id).orElseThrow {
            SnippetNotFound("Snippet not found when trying to get it")
        }
        val content = assetService.get("snippets", id)
        return FullSnippet(snippet, content)
    }

    override fun update(id: Long, content: String): FullSnippet {
        checkIfExists(id, "edit")
        val snippet = snippetRepository.findById(id).get()
        assetService.put("snippets", id, content)
        return FullSnippet(snippet, content)
    }

    override fun delete(id: Long) {
        checkIfExists(id, "delete")
        snippetRepository.deleteById(id)
        assetService.delete(id)
    }

    override fun checkIfExists(id: Long, operation: String) {
        if (!snippetRepository.existsById(id)) {
            throw SnippetNotFound("Snippet not found when trying to $operation it")
        }
    }
}

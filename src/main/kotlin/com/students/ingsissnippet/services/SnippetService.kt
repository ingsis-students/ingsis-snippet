package com.students.ingsissnippet.services

import com.students.ingsissnippet.dtos.request_types.Compliance
import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.dtos.response_dtos.FullSnippet
import com.students.ingsissnippet.dtos.response_dtos.SnippetDTO
import com.students.ingsissnippet.dtos.response_dtos.SnippetUserDto
import com.students.ingsissnippet.dtos.response_dtos.SnippetWithRole
import com.students.ingsissnippet.errors.SnippetNotFound
import com.students.ingsissnippet.repositories.SnippetRepository
import com.students.ingsissnippet.routes.SnippetServiceRoutes
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class SnippetService(
    private val snippetRepository: SnippetRepository,
    private val permissionService: PermissionService,
    private val assetService: AssetService,
    private val languageService: LanguageService,
    private val parseService: ParseService
) : SnippetServiceRoutes {

    override fun create(name: String, content: String, languageId: String, owner: String, token: String): FullSnippet {
        val language = languageService.getLanguageById(languageId.toLongOrNull())
        val snippet = Snippet(name = name, language = language, owner = owner)
        snippetRepository.save(snippet)
        assetService.put("snippets", snippet.id, content)
        permissionService.addSnippetToUser(token, owner, snippet.id, "Owner")
        val errors = parseService.validate(snippet.id)
        return FullSnippet(snippet, content, errors)
    }

    override fun get(id: Long): FullSnippet {
        println("ID HERE $id")
        val snippet = snippetRepository.findById(id)
            .orElseThrow { SnippetNotFound("Snippet not found when trying to get it") }
        println("SNIPPET HERE $snippet")
        val content = assetService.get("snippets", id)
        println("CONTENT HERE $content")
        return FullSnippet(snippet, content)
    }

    fun getSnippets(page: Int, pageSize: Int, snippetName: String?): List<SnippetDTO> {
        val pageable = PageRequest.of(page, pageSize)
        val snippets = if (!snippetName.isNullOrEmpty()) {
            snippetRepository.findByNameContainingIgnoreCase(snippetName, pageable).content
        } else {
            snippetRepository.findAll(pageable).content
        }
        return snippets.map { snippet -> SnippetDTO(snippet) }
    }

    fun getSnippetsOfUser(page: Int, pageSize: Int, snippetsIds: List<SnippetUserDto>): List<SnippetWithRole> {
        val pageable = PageRequest.of(page, pageSize)

        val snippetIdToRoleMap = snippetsIds.associateBy({ it.snippetId }, { it.role })

        if (snippetIdToRoleMap.isEmpty()) return emptyList()

        val snippets = snippetRepository.findByIdIn(snippetIdToRoleMap.keys, pageable).content

        return snippets.map {
            SnippetWithRole(
                snippet = it,
                role = snippetIdToRoleMap[it.id] ?: "Default"
            )
        }
    }

    override fun update(id: Long, content: String): FullSnippet {
        checkIfExists(id, "edit")
        val snippet = snippetRepository.findById(id).get()
        assetService.put("snippets", id, content)
        return FullSnippet(snippet, content)
    }

    override fun delete(directory: String, id: Long) {
        checkIfExists(id, "delete")
        snippetRepository.deleteById(id)
        assetService.delete(directory, id)
    }

    override fun checkIfExists(id: Long, operation: String) {
        if (!snippetRepository.existsById(id)) {
            throw SnippetNotFound("Snippet not found when trying to $operation it")
        }
    }

    fun countSnippets(snippetName: String?): Long {
        return if (!snippetName.isNullOrEmpty()) {
            snippetRepository.countByNameContainingIgnoreCase(snippetName)
        } else {
            snippetRepository.count()
        }
    }

    fun updateStatus(id: Long, status: Compliance): FullSnippet {
        val snippet = snippetRepository.findById(id).orElseThrow {
            RuntimeException("Snippet with ID $id not found")
        }
        snippet.status = status
        println("HIT snippet service: snippet $id status was updated to $status")

        val updatedSnippet = snippetRepository.save(snippet)
        return FullSnippet(updatedSnippet, assetService.get("snippets", id))
    }
}

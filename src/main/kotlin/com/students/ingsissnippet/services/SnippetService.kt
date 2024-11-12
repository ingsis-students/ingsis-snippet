package com.students.ingsissnippet.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.students.ingsissnippet.dtos.request_types.Compliance
import com.students.ingsissnippet.dtos.response_dtos.SnippetWithRoleAndWarnings
import com.students.ingsissnippet.dtos.response_dtos.SnippetDTO
import com.students.ingsissnippet.dtos.response_dtos.SnippetUserDto
import com.students.ingsissnippet.dtos.response_dtos.FullSnippet
import com.students.ingsissnippet.entities.Snippet
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
        val errors = parseService.validate(token, snippet.language.version, content)
        if (errors.isNotEmpty()) { return FullSnippet(snippet, content, errors) }
        snippetRepository.save(snippet)
        assetService.put("snippets", snippet.id, content)
        permissionService.addSnippetToUser(token, owner, snippet.id, "Owner")
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

    fun getFilteredSnippets(
        page: Int,
        pageSize: Int,
        snippetsIds: List<SnippetUserDto>,
        snippetName: String?,
        roles: List<String>?,
        languages: List<Long>?,
        compliance: List<Compliance>?
    ): Pair<List<SnippetWithRoleAndWarnings>, Long> {
        val snippetIdToRoleMap = snippetsIds.associateBy({ it.snippetId }, { it.role })
        if (snippetIdToRoleMap.isEmpty()) return Pair(emptyList(), 0)
        val filteredSnippetIdToRoleMap = if (!roles.isNullOrEmpty()) {
            snippetIdToRoleMap.filter { entry -> roles.contains(entry.value) }
        } else {
            snippetIdToRoleMap
        }

        val snippets = snippetRepository.findAllById(filteredSnippetIdToRoleMap.keys)

        val snippetsWithWarnings = snippets.map { snippet ->
            val warningsJson = assetService.get("lint-warnings", snippet.id)
            val warnings = try {
                jacksonObjectMapper().readValue<List<String>>(warningsJson, object : TypeReference<List<String>>() {})
            } catch (e: Exception) {
                println("Error deserializing warnings for snippet ${snippet.id}: ${e.message}")
                emptyList<String>()
            }

            SnippetWithRoleAndWarnings(
                snippet = snippet,
                role = snippetIdToRoleMap[snippet.id] ?: "Default",
                warnings = warnings,
            )
        }
        val snippetIdToWarnings = snippetsWithWarnings.associateBy({it.id}, {it.lintWarnings})

        val filteredSnippets = snippets.filter { snippet ->
            (snippetName == null || snippet.name.contains(snippetName, ignoreCase = true)) &&
                (languages.isNullOrEmpty() || languages.contains(snippet.language.id)) &&
                (compliance.isNullOrEmpty() || compliance.contains(snippet.status))
        }

        val totalCount = filteredSnippets.size.toLong()

        val pagedSnippets = filteredSnippets
            .drop(page * pageSize)
            .take(pageSize)
            .map { snippet ->
                val role = filteredSnippetIdToRoleMap[snippet.id]!!
                val warnings = snippetIdToWarnings[snippet.id]!!
                SnippetWithRoleAndWarnings(snippet, role, warnings)
            }

        return Pair(pagedSnippets, totalCount)
    }

    override fun update(id: Long, content: String, token: String): FullSnippet {
        checkIfExists(id, "edit")
        val snippet = snippetRepository.findById(id).get()
        val errors = parseService.validate(token, snippet.language.version, content)
        if (errors.isNotEmpty()) { return FullSnippet(snippet, content, errors) }
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

    fun format(id: Long, content: String, token: String): String {
        val userId = permissionService.validate(token).body!!
        val snippet = get(id)
        val version = snippet.version
        val formatRules = assetService.get("format-rules", userId)
        println("por enviar al parseService la solicitud de formateo con reglas: $formatRules")
        return parseService.format(version, content, formatRules, token)
    }
}

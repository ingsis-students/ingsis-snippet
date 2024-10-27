package com.students.ingsissnippet.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.students.ingsissnippet.config.SnippetMessage
import com.students.ingsissnippet.config.producers.LinterRuleProducer
import com.students.ingsissnippet.dtos.request_types.Rule
import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.dtos.response_dtos.FullSnippet
import com.students.ingsissnippet.entities.Language
import com.students.ingsissnippet.errors.SnippetNotFound
import com.students.ingsissnippet.factories.RuleFactory
import com.students.ingsissnippet.repositories.SnippetRepository
import com.students.ingsissnippet.routes.SnippetServiceRoutes
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class SnippetService(
    private val snippetRepository: SnippetRepository,
    private val permissionService: PermissionService,
    private val assetService: AssetService,
    private val linterRuleProducer: LinterRuleProducer,
    ) : SnippetServiceRoutes {

    override fun create(name: String, content: String, language: Language, owner: String): FullSnippet {
        val snippet = Snippet(name = name, language = language, owner = owner)
        assetService.put("snippets", snippet.id, content)
        snippetRepository.save(snippet)
        permissionService.addSnippetToUser(owner, snippet.id, "Owner")
        return FullSnippet(snippet, content)
    }

    override fun get(id: Long): FullSnippet {
        val snippet = snippetRepository.findById(id).orElseThrow { NoSuchElementException("Snippet not found") }
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

    suspend fun lintSnippets(userId: Long, lintRules: List<Rule>): List<Rule> {
        val mapper = jacksonObjectMapper()

        // agrego las rules como json
        val jsonRules = mapper.writeValueAsString(lintRules)
        assetService.put("lint-rules", userId, jsonRules)

        // agarro las rules y las parseo como lista de rules
        val updatedRulesJson = assetService.get("lint-rules", userId)
        val updatedRules: List<Rule> = mapper.readValue(updatedRulesJson, object : TypeReference<List<Rule>>() {})

        val snippets: List<Snippet> = permissionService.getSnippets(userId).body!!

        snippets.forEach { snippet ->
            val msg = SnippetMessage(
                snippetId = snippet.id,
                userId = userId
            )
            linterRuleProducer.publishEvent(msg)
        }
        return updatedRules
    }

    override fun setDefaultLintRules(userId: Long): String {
        val defaultRules: List<Rule> = RuleFactory.defaultLintRules()
        val mapper = jacksonObjectMapper()
        val jsonRules = mapper.writeValueAsString(defaultRules)
        assetService.put("lint-rules", userId, jsonRules)
        return jsonRules
    }

    override fun setDefaultFormatRules(userId: Long): String {
        val defaultRules: List<Rule> = RuleFactory.defaultFormatRules()
        val mapper = jacksonObjectMapper()
        val jsonRules = mapper.writeValueAsString(defaultRules)
        assetService.put("format-rules", userId, jsonRules)
        return jsonRules
    }

}

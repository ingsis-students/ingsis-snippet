package com.students.ingsissnippet.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.students.ingsissnippet.config.SnippetMessage
import com.students.ingsissnippet.config.producers.LinterRuleProducer
import com.students.ingsissnippet.dtos.request_types.Rule
import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.factories.RuleFactory
import org.springframework.stereotype.Service

@Service
class RulesService(
    private val assetService: AssetService,
    private val permissionService: PermissionService,
    private val linterRuleProducer: LinterRuleProducer
) {
    fun getRules(directory: String, userId: Long): List<Rule> {
        println("userIdddd $userId")
        if (!assetService.exists(directory, userId)) {
            print("rules don't exist!!!!!")
            return emptyList()
        }
        val rulesJson = assetService.get(directory, userId)
        print("got the following rules supposed to be json: $rulesJson")
        val mapper = jacksonObjectMapper()
        return mapper.readValue(rulesJson, object : TypeReference<List<Rule>>() {}) // return as List<Rule>
    }

    fun putRules(directory: String, userId: Long, rules: List<Rule>): String {
        if (assetService.exists(directory, userId)) {
            print("rules already exist!!!!!")
            return assetService.get(directory, userId)
        }

        val mapper = jacksonObjectMapper()
        val jsonRules = mapper.writeValueAsString(rules)
        assetService.put(directory, userId, jsonRules)
        print("rules created to user id: $userId")
        return jsonRules
    }

    suspend fun lintSnippets(userId: Long, lintRules: List<Rule>): List<Rule> {
        // agrego las rules como json
        putRules("lint-rules", userId, lintRules)

        // agarro las rules y las parseo como lista de rules
        val updatedRules = getRules("lint-rules", userId)

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

    suspend fun formatSnippets(userId: Long, lintRules: List<Rule>): List<Rule> {
        putRules("format-rules", userId, lintRules)
        val updatedRules = getRules("format-rules", userId)

        val snippets: List<Snippet> = permissionService.getSnippets(userId).body!!

        snippets.forEach { snippet ->
            val msg = SnippetMessage(
                snippetId = snippet.id,
                userId = userId
            )
            linterRuleProducer.publishEvent(msg) // TODO formatRuleProducer.
        }
        return updatedRules
    }

    fun setDefaultLintRules(userId: Long): String {
        val defaultRules: List<Rule> = RuleFactory.defaultLintRules()
        return putRules("lint-rules", userId, defaultRules)
    }

    fun setDefaultFormatRules(userId: Long): String {
        val defaultRules: List<Rule> = RuleFactory.defaultFormatRules()
        return putRules("format-rules", userId, defaultRules)
    }
}

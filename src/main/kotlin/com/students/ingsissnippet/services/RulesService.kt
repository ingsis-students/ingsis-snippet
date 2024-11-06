package com.students.ingsissnippet.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.students.ingsissnippet.config.SnippetMessage
import com.students.ingsissnippet.config.producers.LinterRuleProducer
import com.students.ingsissnippet.dtos.request_types.Rule
import com.students.ingsissnippet.factories.RuleFactory
import org.springframework.stereotype.Service

@Service
class RulesService(
    private val assetService: AssetService,
    private val permissionService: PermissionService,
    private val linterRuleProducer: LinterRuleProducer
) {
    fun getRules(directory: String, userId: Long): List<Rule> {
        if (!assetService.exists(directory, userId)) {
            println("rules don't exist!!!!!")
            return emptyList()
        }
        val rulesJson = assetService.get(directory, userId)
        println("got the following rules supposed to be json: $rulesJson")
        val mapper = jacksonObjectMapper()
        return mapper.readValue(rulesJson, object : TypeReference<List<Rule>>() {}) // return as List<Rule>
    }

    fun putRules(directory: String, userId: Long, rules: List<Rule>): String {
        val mapper = jacksonObjectMapper()
        val jsonRules = mapper.writeValueAsString(rules)
        assetService.put(directory, userId, jsonRules)
        println("rules created to user id: $userId")
        return jsonRules
    }

    suspend fun lintSnippets(token: String, userId: Long, lintRules: List<Rule>): List<Rule> {
        // agrego las rules como json
        putRules("lint-rules", userId, lintRules)

        // agarro las rules y las parseo como lista de rules
        val updatedRules = getRules("lint-rules", userId)
        println("updated rules: $updatedRules")

        val snippetsId: List<Long> = permissionService.getSnippetsId(token, userId).body!!
        println("snippets of the user $userId: $snippetsId")

        snippetsId.forEach { id ->
            val msg = SnippetMessage(
                snippetId = id,
                userId = userId
            )
            linterRuleProducer.publishEvent(msg)
        }
        return updatedRules
    }

    suspend fun formatSnippets(token: String, userId: Long, lintRules: List<Rule>): List<Rule> {
        putRules("format-rules", userId, lintRules)
        val updatedRules = getRules("format-rules", userId)

        val snippetsId: List<Long> = permissionService.getSnippetsId(token, userId).body!!

        snippetsId.forEach { id ->
            val msg = SnippetMessage(
                snippetId = id,
                userId = userId
            )
            linterRuleProducer.publishEvent(msg) // TODO formatRuleProducer being cooked
        }
        return updatedRules
    }

    fun setDefaultLintRules(userId: Long): String {
        val defaultRules: List<Rule> = RuleFactory.defaultLintRules()

        if (assetService.exists("lint-rules", userId)) {
            return assetService.get("lint-rules", userId)
        }
        return putRules("lint-rules", userId, defaultRules)
    }

    fun setDefaultFormatRules(userId: Long): String {
        val defaultRules: List<Rule> = RuleFactory.defaultFormatRules()

        if (assetService.exists("format-rules", userId)) {
            return assetService.get("format-rules", userId)
        }
        return putRules("format-rules", userId, defaultRules)
    }
}

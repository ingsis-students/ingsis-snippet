package com.students.ingsissnippet.controllers

import com.students.ingsissnippet.dtos.request_types.Rule
import com.students.ingsissnippet.services.PermissionService
import com.students.ingsissnippet.services.RulesService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/snippets")
class RulesController(
    private val permissionService: PermissionService,
    private val rulesService: RulesService
) {
    @GetMapping("/lint/rules")
    fun getRules(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<List<Rule>> {
        print("getRulesss $token")
        val userId = permissionService.validate(token).body!!
        val rules = rulesService.getRules("lint-rules", userId)
        return ResponseEntity.ok(rules)
    }

    @GetMapping("/format/rules")
    fun getFormatRules(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<List<Rule>> {
        val userId = permissionService.validate(token).body!!
        val rules = rulesService.getRules("format-rules", userId)
        return ResponseEntity.ok(rules)
    }

    @PostMapping("/lint/rules")
    suspend fun lintSnippets(
        @RequestHeader("Authorization") token: String,
        @RequestBody lintRules: List<Rule>
    ): ResponseEntity<List<Rule>> {
        val userId = permissionService.validate(token).body!!
        val updatedRules = rulesService.lintSnippets(userId, lintRules)
        return ResponseEntity.ok(updatedRules)
    }

    @PostMapping("/format/rules")
    suspend fun formatSnippets(
        @RequestHeader("Authorization") token: String,
        @RequestBody formatRules: List<Rule>
    ): ResponseEntity<List<Rule>> {
        val userId = permissionService.validate(token).body!!
        val updatedRules = rulesService.formatSnippets(userId, formatRules)
        return ResponseEntity.ok(updatedRules)
    }

    @PostMapping("/rules/default")
    fun setDefaultRules(
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<String> {
        val userId = permissionService.validate(token).body!!

        val lintRules = rulesService.setDefaultLintRules(userId)
        val formatRules = rulesService.setDefaultFormatRules(userId)

        return ResponseEntity.ok("Default rules set for user $userId: $lintRules, $formatRules")
    }

    @PostMapping("/lint/rules/default")
    fun setDefaultLintRules(
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<String> {
        val userId = permissionService.validate(token).body!!
        val jsonRules = rulesService.setDefaultLintRules(userId)
        return ResponseEntity.ok(jsonRules)
    }

    @PostMapping("/format/rules/default")
    fun setDefaultFormatRules(
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<String> {
        val userId = permissionService.validate(token).body!!
        val jsonRules = rulesService.setDefaultFormatRules(userId)
        return ResponseEntity.ok(jsonRules)
    }
}

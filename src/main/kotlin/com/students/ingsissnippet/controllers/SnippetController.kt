package com.students.ingsissnippet.controllers

import com.students.ingsissnippet.config.SnippetMessage
import com.students.ingsissnippet.config.producers.LinterRuleProducer
import com.students.ingsissnippet.dtos.request_types.ContentRequest
import com.students.ingsissnippet.dtos.request_types.ShareRequest
import com.students.ingsissnippet.dtos.request_types.SnippetRequest
import com.students.ingsissnippet.dtos.response_dtos.FullSnippet
import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.services.ParseService
import com.students.ingsissnippet.services.PermissionService
import com.students.ingsissnippet.services.SnippetService
import kotlinx.serialization.json.JsonObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping

@RestController
@RequestMapping("/api/snippets")
class SnippetController(
    private val snippetService: SnippetService,
    private val permissionService: PermissionService,
    private val parseService: ParseService,
    private val linterRuleProducer: LinterRuleProducer
) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<FullSnippet> {
        val fullSnippet = snippetService.get(id)
        return ResponseEntity.ok(fullSnippet)
    }

    @PostMapping("/")
    fun create(@RequestBody snippetRequest: SnippetRequest): ResponseEntity<FullSnippet> {
        val fullSnippet = snippetService.create(
            snippetRequest.name, snippetRequest.content, snippetRequest.language, snippetRequest.owner
        )
        return ResponseEntity.ok(fullSnippet)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody req: ContentRequest): ResponseEntity<FullSnippet> {
        val fullSnippet = snippetService.update(id, req.content)
        return ResponseEntity.ok(fullSnippet)
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        snippetService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/format/{id}")
    fun format(@PathVariable id: Long): ResponseEntity<FullSnippet> {
        val fullSnippet = parseService.format(id)
        return ResponseEntity.ok(fullSnippet)
    }

    @PostMapping("/execute/{id}")
    fun execute(@PathVariable id: Long): ResponseEntity<String> {
        val output = parseService.execute(id)
        return ResponseEntity.ok(output)
    }

    @PostMapping("/validate/{id}")
    fun validate(@PathVariable id: Long): String {
        return parseService.validate(id)
    }

    @PostMapping("/lint/{id}")
    fun lint(@PathVariable id: Long): String {
        return parseService.analyze(id)
    }

    @PostMapping("/share/{id}")
    fun share(@PathVariable id: Long, @RequestBody emails: ShareRequest): ResponseEntity<String> {
        return permissionService.shareSnippet(id, emails.fromEmail, emails.toEmail)
    }

    @PostMapping("/lint/rules")
    suspend fun lintSnippets(
        @RequestHeader("Authorization") token: String,
        @RequestBody lintRules: JsonObject
    ): ResponseEntity<String> {
        val userId = permissionService.validate(token)
        val snippets: List<Snippet> = permissionService.getSnippets(userId.body!!).body!!

        snippets.forEach { snippet ->
            val msg = SnippetMessage(
                snippetId = snippet.id,
                rules = lintRules
            )
            linterRuleProducer.publishEvent(msg)
        }

        return ResponseEntity.ok("Snippets submitted for linting")
    }
}

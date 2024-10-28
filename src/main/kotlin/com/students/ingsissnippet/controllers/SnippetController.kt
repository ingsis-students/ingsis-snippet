package com.students.ingsissnippet.controllers

import com.students.ingsissnippet.dtos.request_types.ContentRequest
import com.students.ingsissnippet.dtos.request_types.ShareRequest
import com.students.ingsissnippet.dtos.request_types.SnippetRequest
import com.students.ingsissnippet.dtos.response_dtos.FullSnippet
import com.students.ingsissnippet.services.ParseService
import com.students.ingsissnippet.services.PermissionService
import com.students.ingsissnippet.services.SnippetService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/snippets")
class SnippetController(
    private val snippetService: SnippetService,
    private val permissionService: PermissionService,
    private val parseService: ParseService,
) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<FullSnippet> {
        val fullSnippet = snippetService.get(id)
        return ResponseEntity.ok(fullSnippet)
    }

    @GetMapping
    fun getAll(
        @RequestParam page: Int = 0,
        @RequestParam pageSize: Int = 10,
        @RequestParam(required = false) snippetName: String?
    ): ResponseEntity<Map<String, Any>> {
        val snippets = snippetService.getSnippets(page, pageSize, snippetName)
        val totalCount = snippetService.countSnippets(snippetName)
        return ResponseEntity.ok(mapOf("snippets" to snippets, "count" to totalCount))
    }

    @PostMapping("/")
    fun create(
        @RequestBody snippetRequest: SnippetRequest,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<FullSnippet> {
        val fullSnippet = snippetService.create(
            snippetRequest.name, snippetRequest.content, snippetRequest.language, snippetRequest.owner, token
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

    @PostMapping("/share/{id}")
    fun share(
        @RequestHeader("Authorization") token: String,
        @PathVariable id: Long,
        @RequestBody emails: ShareRequest
    ): ResponseEntity<String> {
        return permissionService.shareSnippet(token, id, emails.fromEmail, emails.toEmail)
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
}

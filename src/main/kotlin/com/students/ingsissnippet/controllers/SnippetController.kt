package com.students.ingsissnippet.controllers

import com.students.ingsissnippet.dtos.request_types.ContentRequest
import com.students.ingsissnippet.dtos.request_types.ShareRequest
import com.students.ingsissnippet.dtos.request_types.SnippetRequest
import com.students.ingsissnippet.dtos.response_dtos.FullSnippet
import com.students.ingsissnippet.services.ParseService
import com.students.ingsissnippet.services.PermissionService
import com.students.ingsissnippet.services.SnippetService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
            snippetRequest.name, snippetRequest.content, snippetRequest.languageId, snippetRequest.owner, token
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
        snippetService.delete("snippets", id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/share/{id}")
    fun share(
        @RequestHeader("Authorization") token: String,
        @PathVariable id: Long,
        @RequestBody emails: ShareRequest
    ): ResponseEntity<FullSnippet> {

        val snippet = try {
            snippetService.get(id)
        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header("Share-Status", "Snippet not found while trying to share it")
                .body(FullSnippet())
        }

        return permissionService.shareSnippet(token, id, emails.fromEmail, emails.toEmail, snippet)
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
